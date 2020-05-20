package com.castle.net.tcp;

import com.castle.net.Connection;
import com.castle.net.Connector;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closer;
import com.castle.util.function.ThrowingFunction;
import com.castle.util.function.ThrowingSupplier;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicReference;

public class TcpServerConnector implements Connector {

    private final ThrowingSupplier<? extends ServerSocket, ? extends IOException> mSocketCreator;
    private final ThrowingFunction<ServerSocket, ? extends Socket, IOException> mConnectionAcceptor;

    private final AtomicReference<ServerSocket> mServerSocketReference;

    public TcpServerConnector(ThrowingSupplier<? extends ServerSocket, ? extends IOException> socketCreator,
                              ThrowingFunction<ServerSocket, ? extends Socket, IOException> connectionAcceptor) {
        mSocketCreator = socketCreator;
        mConnectionAcceptor = connectionAcceptor;
        mServerSocketReference = new AtomicReference<>();
    }

    public TcpServerConnector(int serverPort, int readTimeoutMs) {
        this(()-> {
            ServerSocket serverSocket = new ServerSocket(serverPort);
            serverSocket.setSoTimeout(readTimeoutMs);
            return serverSocket;
        }, (serverSocket)-> {
            Socket socket = serverSocket.accept();
            return Closer.with(socket).run(()-> {
                socket.setSoTimeout(readTimeoutMs);
                return socket;
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        });
    }

    @Override
    public Connection connect(long timeoutMs) throws IOException, TimeoutException {
        ServerSocket serverSocket = getServerSocket();
        try {
            Socket socket = mConnectionAcceptor.apply(serverSocket);
            return new TcpSocketConnection(socket);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        ServerSocket serverSocket = mServerSocketReference.getAndSet(null);
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    private synchronized ServerSocket getServerSocket() throws IOException {
        ServerSocket serverSocket = mServerSocketReference.get();
        if (serverSocket != null) {
            return serverSocket;
        }

        serverSocket = mSocketCreator.get();
        mServerSocketReference.set(serverSocket);
        return serverSocket;
    }
}
