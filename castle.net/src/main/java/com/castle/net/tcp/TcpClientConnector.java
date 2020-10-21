package com.castle.net.tcp;

import com.castle.annotations.NotThreadSafe;
import com.castle.net.Connector;
import com.castle.net.StreamConnection;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closer;
import com.castle.util.function.ThrowingSupplier;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

@NotThreadSafe
public class TcpClientConnector implements Connector<StreamConnection> {

    private final ThrowingSupplier<? extends Socket, ? extends IOException> mSocketCreator;
    private final SocketAddress mEndPoint;

    public TcpClientConnector(ThrowingSupplier<? extends Socket, ? extends IOException> socketCreator,
                              SocketAddress endPoint) {
        mSocketCreator = socketCreator;
        mEndPoint = endPoint;
    }

    public TcpClientConnector(SocketAddress endPoint, int readTimeoutMs) {
        this(() -> {
            Socket socket = new Socket();
            socket.setSoTimeout(readTimeoutMs);
            return socket;
        }, endPoint);
    }

    @Override
    public StreamConnection connect(long timeoutMs) throws IOException, TimeoutException {
        Closer closer = Closer.empty();
        try {
            Socket socket = mSocketCreator.get();
            closer.add(socket);

            return closer.<StreamConnection, IOException>run(() -> {
                socket.connect(mEndPoint, (int) timeoutMs);
                return new TcpSocketConnection(socket);
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
