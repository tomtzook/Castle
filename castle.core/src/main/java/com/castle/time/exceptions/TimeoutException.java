package com.castle.time.exceptions;

public class TimeoutException extends Exception {

    public TimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public TimeoutException(Throwable cause) {
        super(cause);
    }

    public TimeoutException(String message) {
        super(message);
    }

    public TimeoutException() {
        super();
    }
}
