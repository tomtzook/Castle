package com.castle.util;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Collections {

    public static List<Object> genericArrayToList(Object arr) {
        int length = Array.getLength(arr);

        List<Object> list = new ArrayList<>(length);

        for (int i = 0; i < length; i++) {
            Object value = Array.get(arr, i);
            list.add(value);
        }

        return list;
    }
}
