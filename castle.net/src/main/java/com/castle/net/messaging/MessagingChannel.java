package com.castle.net.messaging;

import com.castle.time.exceptions.TimeoutException;

import java.io.Closeable;
import java.io.IOException;

public interface MessagingChannel extends Closeable {

    void write(RawMessage message) throws IOException;
    RawMessage read() throws IOException, TimeoutException;
}
