package com.castle.exceptions;

public class CodeLoadException extends Exception {

    public CodeLoadException(String message, Throwable cause) {
        super(message, cause);
    }

    public CodeLoadException(Throwable cause) {
        super(cause);
    }

    public CodeLoadException(String message) {
        super(message);
    }

    public CodeLoadException() {
        super();
    }
}
