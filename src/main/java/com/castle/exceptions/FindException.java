package com.castle.exceptions;

public class FindException extends Exception {

    public FindException(String message, Throwable cause) {
        super(message, cause);
    }

    public FindException(Throwable cause) {
        super(cause);
    }

    public FindException(String message) {
        super(message);
    }

    public FindException() {
        super();
    }
}
