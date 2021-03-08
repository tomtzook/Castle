package com.castle.net.messaging;

public class MessageHeader {

    private final int mVersion;
    private final int mType;
    private final int mDataSize;

    public MessageHeader(int version, int type, int dataSize) {
        mVersion = version;
        mType = type;
        mDataSize = dataSize;
    }

    public int getVersion() {
        return mVersion;
    }

    public int getType() {
        return mType;
    }

    public int getDataSize() {
        return mDataSize;
    }
}
