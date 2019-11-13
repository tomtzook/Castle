package com.castle.repository.exceptions;

public class KeyNotInRepositoryException extends RepositoryException {

    public KeyNotInRepositoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public KeyNotInRepositoryException(Throwable cause) {
        super(cause);
    }

    public KeyNotInRepositoryException(String message) {
        super(message);
    }

    public KeyNotInRepositoryException() {
    }
}
