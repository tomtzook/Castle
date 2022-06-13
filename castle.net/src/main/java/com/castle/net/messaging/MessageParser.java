package com.castle.net.messaging;

import java.io.IOException;

public interface MessageParser {

    Message parse(RawMessage rawMessage) throws IOException;
}
