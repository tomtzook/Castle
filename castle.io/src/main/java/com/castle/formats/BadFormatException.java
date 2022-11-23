package com.castle.formats;

import java.io.IOException;

public class BadFormatException extends IOException {

    public BadFormatException(String message) {
        super(message);
    }

    public BadFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
