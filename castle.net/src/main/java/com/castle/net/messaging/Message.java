package com.castle.net.messaging;

import java.io.IOException;
import java.io.InputStream;

public interface Message {

    MessageType type();

    InputStream openContent() throws IOException;
}
