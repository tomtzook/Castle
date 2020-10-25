package com.castle.net;

import java.io.IOException;
import java.nio.channels.Channel;

public interface ChannelConnection<T extends Channel> extends Connection {

    T channel() throws IOException;

    @Override
    void close() throws IOException;
}
