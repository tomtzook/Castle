package com.castle.store.exceptions;

public class KeyNotFoundException extends Exception {

    public KeyNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyNotFoundException(Throwable cause) {
        super(cause);
    }

    public KeyNotFoundException(String message) {
        super(message);
    }

    public KeyNotFoundException() {
        super();
    }
}
