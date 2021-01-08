package com.castle.net.nio;

import com.castle.net.ChannelConnection;
import com.castle.time.Time;
import com.castle.util.closeables.Closer;
import org.junit.internal.Throwables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class SocketChannelInstrumentationTest {

    private static final Time DEFAULT_CONNECTION_TIMEOUT = Time.milliseconds(1000);

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
    public void connecting_clientAndServer_success() throws Throwable {
        byte[] DATA = {0x1, 0x3};

        ThrowingConsumer<ChannelConnection<SocketChannel>> serverTask = (serverConnection) -> {
            ByteBuffer data = ByteBuffer.allocate(DATA.length);
            serverConnection.channel().configureBlocking(true);
            serverConnection.channel().read(data);

            assertArrayEquals(DATA, data.array());
        };
        ThrowingConsumer<ChannelConnection<SocketChannel>> clientTask = (clientConnection) -> {
            clientConnection.channel().write(ByteBuffer.wrap(DATA));
        };

        connectAndRun(serverTask, clientTask, DEFAULT_CONNECTION_TIMEOUT);
    }

    private void connectAndRun(ThrowingConsumer<? super ChannelConnection<SocketChannel>> serverTask,
                               ThrowingConsumer<? super ChannelConnection<SocketChannel>> clientTask,
                               Time connectionTimeout) throws Throwable {
        ServerSocketChannel serverSocket = ServerSocketChannel.open();
        serverSocket.bind(new InetSocketAddress(0));
        mCloser.add(serverSocket);

        ServerSocketConnector serverConnector = new ServerSocketConnector(serverSocket);
        mCloser.add(serverConnector);
        SocketConnector clientConnector = new SocketConnector(new InetSocketAddress(serverSocket.socket().getLocalPort()));
        mCloser.add(clientConnector);

        CountDownLatch connectionLatch = new CountDownLatch(1);
        CyclicBarrier endTasksBarrier = new CyclicBarrier(2);

        Future<Void> clientFuture = mExecutorService.submit(() -> {
            connectionLatch.await();
            try (ChannelConnection<SocketChannel> connection = clientConnector.connect(connectionTimeout)) {
                clientTask.accept(connection);
            } catch (Throwable throwable) {
                throw new Exception(throwable);
            } finally {
                endTasksBarrier.await();
            }

            return null;
        });

        connectionLatch.countDown();
        try (ChannelConnection<SocketChannel> connection = serverConnector.connect(connectionTimeout)) {
            serverTask.accept(connection);
        } finally {
            endTasksBarrier.await();
        }

        // to throw an exception if necessary
        try {
            clientFuture.get();
        } catch (ExecutionException e) {
            throw Throwables.rethrowAsException(e.getCause());
        }
    }
}
