package com.castle.net;

import com.castle.time.exceptions.TimeoutException;

import java.io.IOException;

public interface PacketConnection extends Connection {

    void write(byte[] bytes, int start, int length) throws IOException;

    void write(byte[] bytes) throws IOException;

    int readInto(byte[] buffer) throws IOException, TimeoutException;

    byte[] read(int amount) throws IOException, TimeoutException;

    @Override
    void close() throws IOException;
}
