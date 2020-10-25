package com.castle.net.tcp;

import com.castle.net.ChannelConnection;
import com.castle.net.Connector;
import com.castle.net.nio.ChannelConnectionImpl;
import com.castle.time.Time;
import com.castle.time.exceptions.TimeoutException;
import com.castle.util.function.ThrowingFunction;
import com.castle.util.function.ThrowingSupplier;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicReference;

public class ServerSocketConnector implements Connector<ChannelConnection<SocketChannel>> {

    private final ThrowingSupplier<? extends ServerSocketChannel, ? extends IOException> mSocketCreator;
    private final ThrowingFunction<ServerSocketChannel, ? extends SocketChannel, IOException> mConnectionAcceptor;

    private final AtomicReference<ServerSocketChannel> mServerSocketReference;

    public ServerSocketConnector(ThrowingSupplier<? extends ServerSocketChannel, ? extends IOException> socketCreator,
                                 ThrowingFunction<ServerSocketChannel, ? extends SocketChannel, IOException> connectionAcceptor) {
        mSocketCreator = socketCreator;
        mConnectionAcceptor = connectionAcceptor;

        mServerSocketReference = new AtomicReference<>();
    }

    public ServerSocketConnector(ServerSocketChannel channel) {
        this(() -> channel, ServerSocketChannel::accept);
    }

    public ServerSocketConnector(int serverPort) {
        this(() -> {
                ServerSocketChannel channel = ServerSocketChannel.open();
                channel.configureBlocking(true);
                channel.bind(new InetSocketAddress(serverPort));
                return channel;
            },
            ServerSocketChannel::accept
        );
    }

    @Override
    public ChannelConnection<SocketChannel> connect(Time timeout) throws IOException, TimeoutException {
        ServerSocketChannel serverSocket = getServerSocket();
        try {
            SocketChannel socket = mConnectionAcceptor.apply(serverSocket);
            return new ChannelConnectionImpl<>(socket);
        } catch (SocketTimeoutException e) {
            throw new TimeoutException(e);
        }
    }

    @Override
    public synchronized void close() throws IOException {
        ServerSocketChannel serverSocket = mServerSocketReference.getAndSet(null);
        if (serverSocket != null) {
            serverSocket.close();
        }
    }

    private synchronized ServerSocketChannel getServerSocket() throws IOException {
        ServerSocketChannel serverSocket = mServerSocketReference.get();
        if (serverSocket != null) {
            return serverSocket;
        }

        serverSocket = mSocketCreator.get();
        mServerSocketReference.set(serverSocket);
        return serverSocket;
    }
}
