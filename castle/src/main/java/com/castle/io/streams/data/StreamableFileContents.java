package com.castle.io.streams.data;

import com.castle.annotations.Immutable;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

@Immutable
public class StreamableFileContents implements Streamable {

    private final Path mFilePath;

    public StreamableFileContents(Path filePath) {
        mFilePath = filePath;
    }

    @Override
    public InputStream openRead() throws IOException {
        return new FileInputStream(mFilePath.toFile());
    }

    @Override
    public OutputStream openWrite() throws IOException {
        return new FileOutputStream(mFilePath.toFile());
    }
}
