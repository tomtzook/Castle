package com.castle.net.messaging;

import com.castle.reflect.data.DataType;

public interface MessageType extends DataType<Integer> {

    int key();

    @Override
    default boolean matchesKey(Integer key) {
        return key() == key;
    }

    class Impl implements MessageType {

        private final int mKey;

        public Impl(int key) {
            mKey = key;
        }

        @Override
        public int key() {
            return mKey;
        }
    }
}
