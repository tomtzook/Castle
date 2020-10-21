package com.castle.net.udp;

import com.castle.net.PacketConnection;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closer;
import org.junit.internal.Throwables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class DirectDatagramInstrumentationTest {

    private static final int DEFAULT_READ_TIMEOUT = 1000;
    private static final int DEFAULT_CONNECTION_TIMEOUT = 1000;

    private Closer mCloser;
    private ExecutorService mExecutorService;

    @BeforeEach
    public void setup() throws Exception {
        mCloser = Closer.empty();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    @AfterEach
    public void teardown() throws Exception {
        mCloser.close();
    }

    @Test
    public void connecting_bothSides_success() throws Throwable {
        byte[] DATA = {0x1, 0x3};

        ThrowingConsumer<PacketConnection> side1Task = (connection) -> {
            byte[] data = new byte[DATA.length];
            connection.readInto(data);

            assertArrayEquals(DATA, data);
        };
        ThrowingConsumer<PacketConnection> side2Task = (connection) -> {
            connection.write(DATA);
        };

        connectAndRun(side1Task, side2Task, null, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    @Test
    public void connecting_thirdSide_ignoreThirdSide() throws Throwable {
        byte[] DATA = {0x2, 0x5, 0x1, 0x3};

        ThrowingConsumer<PacketConnection> side1Task = (connection) -> {
            byte[] data = new byte[DATA.length];
            try {
                connection.readInto(data);
                fail("Expected failure");
            } catch (TimeoutException e) {
            }
        };
        ThrowingConsumer<PacketConnection> side2Task = (connection) -> {

        };
        ThrowingConsumer<SocketAddress> side2Address = (address) -> {
            DatagramSocket socket = new DatagramSocket();
            mCloser.add(socket);
            socket.send(new DatagramPacket(DATA, 0, DATA.length, address));
        };

        connectAndRun(side1Task, side2Task, side2Address, DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    @Test
    public void read_oneSideNotSending_throwsTimeoutException() throws Throwable {
        byte[] DATA = {0x1, 0x3};

        ThrowingConsumer<PacketConnection> side1Task = (connection) -> {
            byte[] data = new byte[DATA.length];
            connection.readInto(data);
        };
        ThrowingConsumer<PacketConnection> side2Task = (connection) -> {

        };

        assertThrows(TimeoutException.class, () ->
                connectAndRun(side1Task, side2Task, null,
                        DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT));
    }

    @Test
    public void read_otherSideNotSending_throwsTimeoutException() throws Throwable {
        byte[] DATA = {0x1, 0x3};

        ThrowingConsumer<PacketConnection> side1Task = (connection) -> {

        };
        ThrowingConsumer<PacketConnection> side2Task = (connection) -> {
            byte[] data = new byte[DATA.length];
            connection.readInto(data);
        };

        assertThrows(TimeoutException.class, () ->
                connectAndRun(side1Task, side2Task, null,
                        DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT));
    }

    private void connectAndRun(ThrowingConsumer<? super PacketConnection> side1Task,
                               ThrowingConsumer<? super PacketConnection> side2Task,
                               ThrowingConsumer<? super SocketAddress> side2AddressTask,
                               int connectionTimeout, int readTimeout) throws Throwable {
        DatagramSocket side1Socket = new DatagramSocket(0);
        mCloser.add(side1Socket);
        side1Socket.setSoTimeout(readTimeout);

        DatagramSocket side2Socket = new DatagramSocket(0);
        mCloser.add(side2Socket);
        side2Socket.setSoTimeout(readTimeout);

        DirectDatagramConnector side1Connector = new DirectDatagramConnector(() -> side1Socket, side2Socket.getLocalSocketAddress());
        mCloser.add(side1Connector);
        DirectDatagramConnector side2Connector = new DirectDatagramConnector(() -> side2Socket, side1Socket.getLocalSocketAddress());
        mCloser.add(side2Connector);

        CountDownLatch connectionLatch = new CountDownLatch(1);
        CyclicBarrier endTasksBarrier = new CyclicBarrier(2);

        Future<Void> side2Future = mExecutorService.submit(() -> {
            connectionLatch.await();
            try (PacketConnection connection = side2Connector.connect(connectionTimeout)) {
                side2Task.accept(connection);
                endTasksBarrier.await();
            } catch (Throwable throwable) {
                endTasksBarrier.await();
                throw new Exception(throwable);
            }

            return null;
        });

        connectionLatch.countDown();
        try (PacketConnection connection = side1Connector.connect(connectionTimeout)) {
            side1Task.accept(connection);
            if (side2AddressTask != null) {
                side2AddressTask.accept(side2Socket.getLocalSocketAddress());
            }
            endTasksBarrier.await();
        } catch (Throwable t) {
            endTasksBarrier.await();
            throw t;
        }

        // to throw an exception if necessary
        try {
            side2Future.get();
        } catch (ExecutionException e) {
            throw Throwables.rethrowAsException(e.getCause().getCause());
        }
    }
}
