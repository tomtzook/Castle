package com.castle.nio;

import com.castle.annotations.Stateless;

import java.nio.file.Path;
import java.nio.file.Paths;

@Stateless
public class SystemPaths {

    private SystemPaths() {}

    public static Path tempDirectory() {
        return Paths.get(System.getProperty("java.io.tmpdir"));
    }
}
