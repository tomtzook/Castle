package com.castle.testutil.io;

import com.castle.nio.Providers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;

public class ZipBuilder {

    private final FileSystemProvider mFileSystemProvider;
    private final Path mZipPath;

    public ZipBuilder(FileSystemProvider fileSystemProvider, Path zipPath) {
        mFileSystemProvider = fileSystemProvider;
        mZipPath = zipPath;
    }

    public ZipBuilder(Path zipPath) {
        this(Providers.zipProvider(), zipPath);
    }

    public ZipBuilder addContent(String path, InputStream content) throws IOException {
        ZipHelper.putEntry(mZipPath, path, content);
        return this;
    }

    public ZipBuilder addContent(String path, byte[] content) throws IOException {
        return addContent(path, new ByteArrayInputStream(content));
    }

    public FileSystem build() throws IOException {
        return mFileSystemProvider.newFileSystem(mZipPath, new HashMap<>());
    }
}
