package com.castle.net.nio;

import com.castle.net.ChannelConnection;
import com.castle.net.Connector;
import com.castle.net.nio.ChannelConnectionImpl;
import com.castle.time.Time;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closer;
import com.castle.util.function.ThrowingSupplier;

import java.io.IOException;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.SocketChannel;

public class SocketConnector implements Connector<ChannelConnection<SocketChannel>> {

    private final ThrowingSupplier<? extends SocketChannel, ? extends IOException> mSocketCreator;
    private final SocketAddress mEndPoint;

    public SocketConnector(ThrowingSupplier<? extends SocketChannel, ? extends IOException> socketCreator,
                              SocketAddress endPoint) {
        mSocketCreator = socketCreator;
        mEndPoint = endPoint;
    }

    public SocketConnector(SocketAddress endPoint) {
        this(SocketChannel::open, endPoint);
    }

    @Override
    public ChannelConnection<SocketChannel> connect(Time timeout) throws IOException, TimeoutException {
        Closer closer = Closer.empty();
        try {
            SocketChannel socket = mSocketCreator.get();
            closer.add(socket);

            return closer.<ChannelConnection<SocketChannel>, IOException>run(() -> {
                socket.connect(mEndPoint);
                return new ChannelConnectionImpl<>(socket);
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
