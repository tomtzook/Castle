package com.castle.net.messaging.v1;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class MessageHeader {

    private static final int VERSION = 1;

    private final int mMessageType;
    private final int mContentSize;

    public MessageHeader(int messageType, int contentSize) {
        mMessageType = messageType;
        mContentSize = contentSize;
    }

    public MessageHeader(DataInput dataInput) throws IOException {
        int version = dataInput.readInt();
        if (version != VERSION) {
            throw new IOException("message header version mismatch: " + version);
        }

        mMessageType = dataInput.readInt();
        mContentSize = dataInput.readInt();
    }

    public int getMessageType() {
        return mMessageType;
    }

    public int getContentSize() {
        return mContentSize;
    }

    public void writeTo(DataOutput dataOutput) throws IOException {
        dataOutput.writeInt(VERSION);
        dataOutput.writeInt(mMessageType);
        dataOutput.writeInt(mContentSize);
    }
}
