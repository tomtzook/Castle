package com.castle.net.messaging;

import com.notifier.Event;

public class NewMessageEvent implements Event {

    private final Message mMessage;

    public NewMessageEvent(Message message) {
        mMessage = message;
    }

    public Message getMessage() {
        return mMessage;
    }
}
