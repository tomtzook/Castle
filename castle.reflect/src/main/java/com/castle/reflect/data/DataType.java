package com.castle.reflect.data;

public interface DataType<T> {

    boolean matchesKey(T key);
}
