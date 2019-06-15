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
    private final TempPathGenerator mPathGenerator;

    private final AtomicReference<OpenZip> mOpenZipReference;

    public Zip(FileSystemProvider zipFileSystemProvider, Path zipPath, Map<String, ?> fileSystemEnv, TempPathGenerator pathGenerator) {
        mZipFileSystemProvider = zipFileSystemProvider;
        mZipPath = zipPath;
        mFileSystemEnv = fileSystemEnv;
        mPathGenerator = pathGenerator;

        mOpenZipReference = new AtomicReference<>();
    }

    public Zip(FileSystemProvider zipFileSystemProvider, Path zipPath, Map<String, ?> fileSystemEnv) {
        this(zipFileSystemProvider, zipPath, fileSystemEnv, new TempPathGenerator("zip", "generated"));
    }

    public Zip(Path zipPath) {
        this(new ZipFileSystemProvider(), zipPath, new HashMap<>());
    }

    public synchronized OpenZip open() throws IOException {
        OpenZip openZip = mOpenZipReference.get();
        if (openZip.isOpen()) {
            return openZip;
        }

        mOpenZipReference.set(null);

        FileSystem zipFs = mZipFileSystemProvider.newFileSystem(mZipPath, mFileSystemEnv);
        openZip = new OpenZip(zipFs, mPathGenerator);
        mOpenZipReference.set(openZip);

        return openZip;
    }
}
