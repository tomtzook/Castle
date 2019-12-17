package com.castle.nio;

import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemPaths {

    private SystemPaths() {}

    public static Path tempDirectory() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }
}
