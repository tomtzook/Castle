package com.castle.io.streams.data;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Path;

public class FileData implements MutableStreamableData {

    private final Path mFilePath;

    public FileData(Path filePath) {
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
