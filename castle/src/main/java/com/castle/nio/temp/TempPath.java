package com.castle.nio.temp;

import com.castle.nio.DelegatingPath;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

public class TempPath extends DelegatingPath implements Closeable {

    private final FileSystemProvider mFileSystemProvider;
    private final Path mPath;

    public TempPath(FileSystemProvider fileSystemProvider, Path path) {
        super(path);
        mFileSystemProvider = fileSystemProvider;
        mPath = path;
    }

    public TempPath(Path path) {
        this(path.getFileSystem().provider(), path);
    }

    public Path originalPath() {
        return mPath;
    }

    @Override
    public void close() throws IOException {
        mFileSystemProvider.deleteIfExists(mPath);
    }
}
