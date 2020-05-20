package com.castle.net.udp;

import com.castle.net.Connector;
import com.castle.net.PacketConnection;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.closeables.Closer;
import com.castle.util.function.ThrowingFunction;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;

public class DirectDatagramConnector implements Connector<PacketConnection> {

    private final ThrowingFunction<SocketAddress, ? extends DatagramSocket, ? extends IOException> mSocketCreator;
    private final SocketAddress mBindAddress;
    private final InetSocketAddress mDestination;

    public DirectDatagramConnector(ThrowingFunction<SocketAddress, ? extends DatagramSocket, ? extends IOException> socketCreator,
                                   SocketAddress bindAddress, InetSocketAddress destination) {
        mSocketCreator = socketCreator;
        mBindAddress = bindAddress;
        mDestination = destination;
    }

    public DirectDatagramConnector(SocketAddress bindAddress, InetSocketAddress destination,
                                   int readTimeoutMs) {
        this((address)-> {
            DatagramSocket datagramSocket = new DatagramSocket(address);
            return Closer.with(datagramSocket).run(()->{
                datagramSocket.setSoTimeout(readTimeoutMs);
                return datagramSocket;
            }, IOException.class, Closer.CloseOption.ON_ERROR);
        }, bindAddress, destination);
    }

    @Override
    public PacketConnection connect(long timeoutMs) throws IOException, TimeoutException {
        DatagramSocket socket = mSocketCreator.apply(mBindAddress);
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
