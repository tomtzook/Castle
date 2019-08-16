package com.castle.nio.zip;

import com.castle.nio.Providers;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Zip {

    private final ZipOpener mZipOpener;

    private final AtomicReference<OpenZip> mOpenZipReference;
    private final Object mReferenceMutex;
    private final ZipReferences mZipReferences;

    public Zip(ZipOpener zipOpener) {
        mZipOpener = zipOpener;

        mOpenZipReference = new AtomicReference<>();
        mReferenceMutex = new Object();
        mZipReferences = new ZipReferences(mReferenceMutex);
    }

    public Zip(FileSystemProvider zipFileSystemProvider, Map<String, ?> fileSystemEnv, Path zipPath) {
        this(new PathBasedZipOpener(zipFileSystemProvider, fileSystemEnv, zipPath));
    }

    public Zip(Path zipPath) {
        this(Providers.zipProvider(), new HashMap<>(), zipPath);
    }

    public OpenZip open() throws IOException {
        synchronized (mReferenceMutex) {
            OpenZip openZip = mOpenZipReference.get();
            if (openZip != null && openZip.isOpen()) {
                mZipReferences.incrementReferencesCount();
                return openZip;
            }

            mOpenZipReference.set(null);

            openZip = mZipOpener.open(mZipReferences);
            mOpenZipReference.set(openZip);

            mZipReferences.incrementReferencesCount();
            return openZip;
        }
    }
}
