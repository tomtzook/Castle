package com.castle.nio.zip;

import com.castle.annotations.NotThreadSafe;
import com.castle.util.closeables.ReferenceCounter;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Map;
import java.util.concurrent.locks.Lock;

@NotThreadSafe
public class PathBasedZipOpener<T extends OpenZip> implements ZipOpener<T> {

    private final FileSystemProvider mFileSystemProvider;
    private final Map<String, ?> mFileSystemEnv;
    private final Path mPath;
    private final ToOpenZip<T> mOpenZip;

    public PathBasedZipOpener(FileSystemProvider fileSystemProvider, Map<String, ?> fileSystemEnv, Path path, ToOpenZip<T> openZip) {
        mFileSystemProvider = fileSystemProvider;
        mFileSystemEnv = fileSystemEnv;
        mPath = path;
        mOpenZip = openZip;
    }

    @Override
    public T open(ReferenceCounter referenceCounter, Lock closeLock) throws IOException {
        FileSystem zipFs = mFileSystemProvider.newFileSystem(mPath, mFileSystemEnv);
        return mOpenZip.newInstance(zipFs, referenceCounter, closeLock);
    }
}
