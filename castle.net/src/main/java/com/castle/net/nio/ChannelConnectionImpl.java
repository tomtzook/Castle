package com.castle.net.nio;

import com.castle.net.ChannelConnection;

import java.io.IOException;
import java.nio.channels.Channel;

public class ChannelConnectionImpl<T extends Channel> implements ChannelConnection<T> {

    private final T mChannel;

    public ChannelConnectionImpl(T channel) {
        mChannel = channel;
    }

    @Override
    public T channel() throws IOException {
        return mChannel;
    }

    @Override
    public void close() throws IOException {
        mChannel.close();
    }
}
