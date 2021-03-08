package com.castle.net.messaging;

import com.castle.reflect.data.DataType;

public interface MessageType extends DataType<Integer> {

    String name();
    int key();
}
