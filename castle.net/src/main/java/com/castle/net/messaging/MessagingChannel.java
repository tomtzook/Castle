package com.castle.net.messaging;

import com.castle.time.exceptions.TimeoutException;

import java.io.IOException;

public interface MessagingChannel {

    void send(RawMessage message) throws IOException;
    RawMessage read() throws IOException, TimeoutException;
}
