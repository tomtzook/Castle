package com.castle.nio.zip;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;

public class PathBasedZipOpener implements ZipOpener {

    private final FileSystemProvider mFileSystemProvider;
    private final Map<String, ?> mFileSystemEnv;
    private final Path mPath;

    public PathBasedZipOpener(FileSystemProvider fileSystemProvider, Map<String, ?> fileSystemEnv, Path path) {
        mFileSystemProvider = fileSystemProvider;
        mFileSystemEnv = fileSystemEnv;
        mPath = path;
    }

    @Override
    public OpenZip open(ZipReferences zipReferences) throws IOException {
        FileSystem zipFs = mFileSystemProvider.newFileSystem(mPath, mFileSystemEnv);
        return new OpenZip(zipFs, zipReferences);
    }
}