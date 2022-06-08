package com.castle.nio.jar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.Manifest;

public class JarManifest {

    private final Path mPath;

    public JarManifest(Path path) {
        mPath = path;
    }

    public Manifest read() throws IOException {
        try (InputStream inputStream = Files.newInputStream(mPath)) {
            return new Manifest(inputStream);
        }
    }
}
