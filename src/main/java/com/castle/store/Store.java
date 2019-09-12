package com.castle.store;

import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.store.exceptions.StoreException;

public interface Store {

    void store(KeyPath key, Object value) throws StoreException;
    void storeTree(KeyPath key, ValueNode root) throws StoreException;

    <T> T retreive(KeyPath key, Class<T> valueType) throws StoreException, KeyNotFoundException;
    ValueNode retreiveTree(KeyPath key) throws StoreException, KeyNotFoundException;
}
