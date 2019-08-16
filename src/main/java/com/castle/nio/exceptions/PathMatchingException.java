package com.castle.nio.exceptions;

import java.io.IOException;

public class PathMatchingException extends IOException {

    public PathMatchingException(String message, Throwable cause) {
        super(message, cause);
    }

    public PathMatchingException(Throwable cause) {
        super(cause);
    }

    public PathMatchingException(String message) {
        super(message);
    }

    public PathMatchingException() {
        super();
    }
}
