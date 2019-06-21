package com.castle.nio.zip;

import com.sun.nio.zipfs.ZipFileSystemProvider;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Zip {

    private final FileSystemProvider mZipFileSystemProvider;
    private final Path mZipPath;
    private final Map<String, ?> mFileSystemEnv;
    private final OpenZipFactory mOpenZipFactory;

    private final AtomicReference<OpenZip> mOpenZipReference;
    private final AtomicInteger mZipReferencesCounter;

    public Zip(FileSystemProvider zipFileSystemProvider, Path zipPath, Map<String, ?> fileSystemEnv, OpenZipFactory openZipFactory) {
        mZipFileSystemProvider = zipFileSystemProvider;
        mZipPath = zipPath;
        mFileSystemEnv = fileSystemEnv;
        mOpenZipFactory = openZipFactory;

        mOpenZipReference = new AtomicReference<>();
        mZipReferencesCounter = new AtomicInteger(0);
    }

    public Zip(FileSystemProvider zipFileSystemProvider, Path zipPath, Map<String, ?> fileSystemEnv) {
        this(zipFileSystemProvider, zipPath, fileSystemEnv, new OpenZipFactory());
    }

    public Zip(Path zipPath, OpenZipFactory openZipFactory) {
        this(new ZipFileSystemProvider(), zipPath, new HashMap<>(), openZipFactory);
    }

    public Zip(Path zipPath) {
        this(zipPath, new OpenZipFactory());
    }

    public synchronized OpenZip open() throws IOException {
        OpenZip openZip = mOpenZipReference.get();
        if (openZip != null && openZip.isOpen()) {
            mZipReferencesCounter.incrementAndGet();
            return openZip;
        }

        mOpenZipReference.set(null);

        FileSystem zipFs = mZipFileSystemProvider.newFileSystem(mZipPath, mFileSystemEnv);
        openZip = mOpenZipFactory.create(zipFs, mZipReferencesCounter);
        mOpenZipReference.set(openZip);

        mZipReferencesCounter.incrementAndGet();
        return openZip;
    }
}
