package com.castle.net;

import com.castle.time.exceptions.TimeoutException;

import java.io.Closeable;
import java.io.IOException;

public interface Connector extends Closeable {

    Connection connect(long timeoutMs) throws IOException, TimeoutException;

    @Override
    void close() throws IOException;
}
