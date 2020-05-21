package com.castle.net.udp;

import com.castle.annotations.NotThreadSafe;
import com.castle.net.Connector;
import com.castle.net.PacketConnection;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closer;
import com.castle.util.function.ThrowingSupplier;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

@NotThreadSafe
public class DirectDatagramConnector implements Connector<PacketConnection> {

    private final ThrowingSupplier<? extends DatagramSocket, ? extends IOException> mSocketCreator;
    private final SocketAddress mDestination;

    public DirectDatagramConnector(ThrowingSupplier<? extends DatagramSocket, ? extends IOException> socketCreator,
                                   SocketAddress destination) {
        mSocketCreator = socketCreator;
        mDestination = destination;
    }

    public DirectDatagramConnector(SocketAddress bindAddress, SocketAddress destination,
                                   int readTimeoutMs) {
        this(()-> {
            DatagramSocket datagramSocket = new DatagramSocket(bindAddress);
            return Closer.with(datagramSocket).run(()->{
                datagramSocket.setSoTimeout(readTimeoutMs);
                return datagramSocket;
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        }, destination);
    }

    @Override
    public PacketConnection connect(long timeoutMs) throws IOException, TimeoutException {
        DatagramSocket socket = mSocketCreator.get();
        try {
            return Closer.with(socket).<PacketConnection, IOException>run(()-> {
                socket.connect(mDestination);
                return new DirectDatagramConnection(socket, mDestination);
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public void close() throws IOException {
    }
}
