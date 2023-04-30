package com.castle.util.dependencies;

public interface ObjectFactory {

    <T> T create(Class<T> type);
}
