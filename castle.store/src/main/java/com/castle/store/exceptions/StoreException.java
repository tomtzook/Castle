package com.castle.store.exceptions;

public class StoreException extends Exception {

    public StoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public StoreException(Throwable cause) {
        super(cause);
    }

    public StoreException(String message) {
        super(message);
    }

    public StoreException() {
        super();
    }
}
