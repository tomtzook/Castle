package com.castle.net.messaging;

import com.castle.net.messaging.MessageType;

public class RawMessage {

    private final MessageType mType;
    private final byte[] mContent;

    public RawMessage(MessageType type, byte[] content) {
        mType = type;
        mContent = content;
    }

    public MessageType getType() {
        return mType;
    }

    public byte[] getContent() {
        return mContent;
    }
}
