package com.castle.net.messaging;

import com.notifier.Listener;

public interface MessageListener extends Listener {

    void onNewMessage(NewMessageEvent event);
}
