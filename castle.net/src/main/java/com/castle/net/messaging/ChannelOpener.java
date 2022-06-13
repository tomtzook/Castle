package com.castle.net.messaging;

import java.io.IOException;

public interface ChannelOpener {

    MessagingChannel open() throws IOException;
}
