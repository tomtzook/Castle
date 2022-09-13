package com.castle.util;

import java.nio.ByteBuffer;

public class Buffers {

    public static boolean areSame(ByteBuffer buffer, int offset, byte[] arr) {
        for (int i = 0; i < arr.length; i++) {
            byte value = buffer.get(offset + i);
            if (value != arr[i]) {
                return false;
            }
        }

        return true;
    }
}
