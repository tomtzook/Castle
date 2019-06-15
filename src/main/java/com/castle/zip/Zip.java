package com.castle.zip;

import com.castle.nio.temp.TempPathGenerator;
import com.sun.nio.zipfs.ZipFileSystemProvider;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Zip {

    private final FileSystemProvider mZipFileSystemProvider;
    private final Path mZipPath;
    private final Map<String, ?> mFileSystemEnv;
    private final ZipOpener mZipOpener;

    private final AtomicReference<OpenZip> mOpenZipReference;

    public Zip(FileSystemProvider zipFileSystemProvider, Path zipPath, Map<String, ?> fileSystemEnv, ZipOpener zipOpener) {
        mZipFileSystemProvider = zipFileSystemProvider;
        mZipPath = zipPath;
        mFileSystemEnv = fileSystemEnv;
        mZipOpener = zipOpener;

        mOpenZipReference = new AtomicReference<>();
    }

    public Zip(FileSystemProvider zipFileSystemProvider, Path zipPath, Map<String, ?> fileSystemEnv) {
        this(zipFileSystemProvider, zipPath, fileSystemEnv, new ZipOpener());
    }

    public Zip(Path zipPath, ZipOpener zipOpener) {
        this(new ZipFileSystemProvider(), zipPath, new HashMap<>(), zipOpener);
    }

    public Zip(Path zipPath) {
        this(zipPath, new ZipOpener());
    }

    public synchronized OpenZip open() throws IOException {
        OpenZip openZip = mOpenZipReference.get();
        if (openZip.isOpen()) {
            return openZip;
        }

        mOpenZipReference.set(null);

        FileSystem zipFs = mZipFileSystemProvider.newFileSystem(mZipPath, mFileSystemEnv);
        openZip = mZipOpener.open(zipFs);
        mOpenZipReference.set(openZip);

        return openZip;
    }
}
