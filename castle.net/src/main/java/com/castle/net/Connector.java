package com.castle.net;

import com.castle.time.Time;
import com.castle.time.exceptions.TimeoutException;

import java.io.Closeable;
import java.io.IOException;

public interface Connector<T extends Connection> extends Closeable {

    T connect(Time timeout) throws IOException, TimeoutException;

    @Override
    void close() throws IOException;
}
