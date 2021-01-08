package com.castle.reflect;

public interface Value {

    Object rawValue();

    boolean canConvert(Class<?> type);
    <T> T convert(Class<T> type);
}
