package com.castle.reflect.exceptions;

public class TypeException extends RuntimeException {

    public TypeException(String message, Throwable cause) {
        super(message, cause);
    }

    public TypeException(Throwable cause) {
        super(cause);
    }

    public TypeException(String message) {
        super(message);
    }

    public TypeException() {
        super();
    }
}
