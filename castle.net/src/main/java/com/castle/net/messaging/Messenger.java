package com.castle.net.messaging;

import java.io.IOException;

public interface Messenger {

    void send(Message message) throws IOException;
}
