package com.castle.store.exceptions;

import com.castle.store.KeyPath;

public class StoreException extends Exception {

    private final KeyPath mKey;

    public StoreException(KeyPath key, String message, Throwable cause) {
        super(message, cause);
        mKey = key;
    }

    public StoreException(KeyPath key, Throwable cause) {
        super(key.getPath(), cause);
        mKey = key;
    }

    public StoreException(KeyPath key, String message) {
        super(message);
        mKey = key;
    }

    public StoreException(KeyPath key) {
        super(key.getPath());
        mKey = key;
    }

    public KeyPath getKey() {
        return mKey;
    }
}
