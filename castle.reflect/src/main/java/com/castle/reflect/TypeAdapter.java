package com.castle.reflect;

public interface TypeAdapter<T> {

    <R> boolean canAdapt(Class<R> type);
    <R> R adapt(T t, Class<R> type);
}
