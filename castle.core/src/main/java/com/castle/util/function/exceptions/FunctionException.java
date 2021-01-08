package com.castle.util.function.exceptions;

public class FunctionException extends Exception {

    public FunctionException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionException(Throwable cause) {
        super(cause);
    }

    public FunctionException(String message) {
        super(message);
    }

    public FunctionException() {
        super();
    }
}
