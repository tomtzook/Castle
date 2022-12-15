package com.castle.net.messaging;

import com.castle.time.exceptions.TimeoutException;

import java.io.Closeable;
import java.io.IOException;

public interface MessagingChannel extends Closeable {

    void write(Message message) throws IOException;
    Message read() throws IOException, TimeoutException, InterruptedException;
}
