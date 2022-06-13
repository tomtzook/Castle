package com.castle.net.messaging;

import java.io.IOException;

public interface MessageQueue {

    void queue(Message message) throws IOException;
}
