package com.castle.util.dependencies;

public class ObjectCreationException extends RuntimeException {

    public ObjectCreationException(String message) {
        super(message);
    }

    public ObjectCreationException(String message, Throwable cause) {
        super(cause);
    }
}
