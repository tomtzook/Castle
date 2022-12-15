package com.castle.net.messaging;

import java.io.IOException;

public interface MessageType {

    int getKey();

    RawMessage toRaw(Message message) throws IOException;
    Message fromRaw(RawMessage message) throws IOException;
}
