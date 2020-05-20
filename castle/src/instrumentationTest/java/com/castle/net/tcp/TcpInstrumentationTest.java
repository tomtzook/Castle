package com.castle.net.tcp;

import com.castle.net.Connection;
import com.castle.net.Connector;
import com.castle.net.StreamConnection;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closer;
import org.junit.internal.Throwables;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.ThrowingConsumer;

import java.io.EOFException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class TcpInstrumentationTest {

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
    public void connecting_clientAndServer_success() throws Throwable {
        byte[] DATA = {0x1, 0x3};

        ThrowingConsumer<StreamConnection> serverTask = (serverConnection) -> {
            byte[] data = new byte[DATA.length];
            serverConnection.inputStream().read(data);

            assertArrayEquals(DATA, data);
        };
        ThrowingConsumer<StreamConnection> clientTask = (clientConnection) -> {
            clientConnection.outputStream().write(DATA);
        };

        connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    @Test
    public void read_clientNothingToRead_returnsEOF() throws Throwable {
        final int READ_TIMEOUT = 200;

        ThrowingConsumer<Connection> serverTask = (serverConnection) -> {
        };
        ThrowingConsumer<StreamConnection> clientTask = (clientConnection) -> {
            int result = clientConnection.inputStream().read();
            assertEquals(-1, result);
        };

        connectAndRun(serverTask, clientTask, DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT);
    }

    @Test
    public void read_serverNothingToRead_returnsEOF() throws Throwable {
        final int READ_TIMEOUT = 200;

        ThrowingConsumer<StreamConnection> serverTask = (serverConnection) -> {
            int result = serverConnection.inputStream().read();
            assertEquals(-1, result);
        };
        ThrowingConsumer<Connection> clientTask = (clientConnection) -> {
        };

        connectAndRun(serverTask, clientTask, DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT);
    }

    @Test
    public void read_clientDisconnectedInMiddle_readReturnsEOF() throws Throwable {
        final int READ_TIMEOUT = 200;

        CountDownLatch latch = new CountDownLatch(1);

        ThrowingConsumer<StreamConnection> serverTask = (serverConnection) -> {
            latch.countDown();

            byte[] bytes = new byte[2];
            int result = serverConnection.inputStream().read(bytes);
            assertEquals(-1, result);
        };
        ThrowingConsumer<Connection> clientTask = (clientConnection) -> {
            clientConnection.close();
            latch.countDown();
        };

        connectAndRun(serverTask, clientTask, DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT);
    }

    @Test
    public void read_serverDisconnectedInMiddle_readReturnsEOF() throws Throwable {
        final int READ_TIMEOUT = 500;

        CountDownLatch latch = new CountDownLatch(1);

        ThrowingConsumer<Connection> serverTask = (serverConnection) -> {
            serverConnection.close();
            latch.countDown();
        };
        ThrowingConsumer<StreamConnection> clientTask = (clientConnection) -> {
            latch.await();

            byte[] bytes = new byte[2];
            int result = clientConnection.inputStream().read(bytes);
            assertEquals(-1, result);
        };

        connectAndRun(serverTask, clientTask,
                DEFAULT_CONNECTION_TIMEOUT, READ_TIMEOUT);
    }

    @Test
    public void connect_serverHasNoClient_throwsTimeoutException() throws Exception {
        final int CONNECTION_TIMEOUT = 200;
        ServerSocket serverSocket = new ServerSocket(0);
        mCloser.add(serverSocket);

        try (TcpServerConnector serverConnector = new TcpServerConnector(()->serverSocket, (server)-> {
            Socket socket = server.accept();
            return Closer.with(socket).run(()->{
                socket.setSoTimeout(DEFAULT_READ_TIMEOUT);
                return socket;
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        })) {
            assertThrows(TimeoutException.class, () ->
                    tryConnectExpectFailure(serverConnector, CONNECTION_TIMEOUT));
        }
    }

    @Test
    public void connect_clientHasNoListeningServer_throwsConnectionFailedException() throws Exception {
        final int CONNECTION_TIMEOUT = 200;
        ServerSocket serverSocket = new ServerSocket(0);
        mCloser.add(serverSocket);

        assertThrows(IOException.class, ()->{
            InetSocketAddress address = new InetSocketAddress(serverSocket.getLocalPort() - 1);
            try (TcpClientConnector clientConnector = new TcpClientConnector(address, DEFAULT_READ_TIMEOUT)) {
                tryConnectExpectFailure(clientConnector, CONNECTION_TIMEOUT);
            }
        });
    }

    private void connectAndRun(ThrowingConsumer<? super StreamConnection> serverTask,
                               ThrowingConsumer<? super StreamConnection> clientTask,
                               int connectionTimeout, int readTimeout) throws Throwable {
        ServerSocket serverSocket = new ServerSocket(0);
        mCloser.add(serverSocket);
        serverSocket.setSoTimeout(readTimeout);

        TcpServerConnector serverConnector = new TcpServerConnector(()->serverSocket, (server)-> {
            Socket socket = server.accept();
            return Closer.with(socket).run(()->{
                socket.setSoTimeout(readTimeout);
                return socket;
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        });
        mCloser.add(serverConnector);
        TcpClientConnector clientConnector = new TcpClientConnector(
                new InetSocketAddress(serverSocket.getLocalPort()), readTimeout);
        mCloser.add(clientConnector);

        CountDownLatch connectionLatch = new CountDownLatch(1);
        CyclicBarrier endTasksBarrier = new CyclicBarrier(2);

        Future<Void> clientFuture = mExecutorService.submit(() -> {
            connectionLatch.await();
            try (StreamConnection connection = clientConnector.connect(connectionTimeout)) {
                clientTask.accept(connection);
            } catch (Throwable throwable) {
                throw new Exception(throwable);
            } finally {
                endTasksBarrier.await();
            }

            return null;
        });

        connectionLatch.countDown();
        try (StreamConnection connection = serverConnector.connect(connectionTimeout)) {
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

    private void tryConnectExpectFailure(Connector<? extends Connection> connector, int timeout) throws Exception {
        try (Connection connection = connector.connect(timeout)) {
            fail("should not have connected");
        }
    }
}
