package com.castle.net.messaging;

import com.castle.reflect.data.DataType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public interface Message extends DataType<MessageType> {

    MessageType type();

    InputStream openContent() throws IOException;

    @Override
    default boolean matchesKey(MessageType key) {
        return type().equals(key);
    }

    class Impl implements Message {

        private final MessageType mType;
        private final byte[] mData;

        public Impl(MessageType type, byte[] data) {
            mType = type;
            mData = data;
        }

        @Override
        public MessageType type() {
            return mType;
        }

        @Override
        public InputStream openContent() throws IOException {
            return new ByteArrayInputStream(mData);
        }
    }
}
