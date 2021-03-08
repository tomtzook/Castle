package com.castle.net.messaging;

public class RawMessage {

    private final MessageHeader mHeader;
    private final byte[] mData;

    public RawMessage(MessageHeader header, byte[] data) {
        mHeader = header;
        mData = data;
    }

    public MessageHeader getHeader() {
        return mHeader;
    }

    public byte[] getData() {
        return mData;
    }
}
