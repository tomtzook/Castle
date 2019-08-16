package com.castle.nio.exceptions;

import java.io.IOException;
import java.nio.file.Path;

public class PathNotFoundException extends IOException {

    private final Path mPath;

    public PathNotFoundException(Path path, String message, Throwable cause) {
        super(String.format("%s: %s", path, message), cause);
        mPath = path;
    }

    public PathNotFoundException(Path path, Throwable cause) {
        super(path.toString(), cause);
        mPath = path;
    }

    public PathNotFoundException(Path path, String message) {
        super(String.format("%s: %s", path, message));
        mPath = path;
    }

    public PathNotFoundException(Path path) {
        super(path.toString());
        mPath = path;
    }

    public Path getPath() {
        return mPath;
    }
}
