package com.castle.store.exceptions;

import com.castle.store.KeyPath;

public class KeyNotFoundException extends Exception {

    private final KeyPath mKey;

    public KeyNotFoundException(KeyPath key) {
        super(key.getPath());
        mKey = key;
    }

    public KeyPath getKey() {
        return mKey;
    }
}
