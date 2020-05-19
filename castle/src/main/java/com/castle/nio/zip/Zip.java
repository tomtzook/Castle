package com.castle.nio.zip;

import com.castle.annotations.ThreadSafe;
import com.castle.nio.Providers;
import com.castle.util.closeables.AtomicReferenceCounter;
import com.castle.util.closeables.ReferenceCounter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@ThreadSafe
public class Zip {

    private final ZipOpener mZipOpener;

    private final AtomicReference<OpenZip> mOpenZipReference;
    private final Lock mReferenceLock;
    private final ReferenceCounter mZipReferenceCounter;

    public Zip(ZipOpener zipOpener) {
        mZipOpener = zipOpener;

        mOpenZipReference = new AtomicReference<>();
        mReferenceLock = new ReentrantLock();
        mZipReferenceCounter = new AtomicReferenceCounter();
    }

    public static Zip fromPath(FileSystemProvider zipFileSystemProvider, Map<String, ?> fileSystemEnv, Path zipPath) {
        return new Zip(new PathBasedZipOpener(zipFileSystemProvider, fileSystemEnv, zipPath));
    }

    public static Zip fromPath(Path zipPath) {
        return fromPath(Providers.zipProvider(), new HashMap<>(), zipPath);
    }

    public OpenZip open() throws IOException {
        mReferenceLock.lock();
        try {
            OpenZip openZip = mOpenZipReference.get();
            if (openZip != null && openZip.isOpen()) {
                mZipReferenceCounter.increment();
                return openZip;
            }

            mOpenZipReference.set(null);

            openZip = mZipOpener.open(mZipReferenceCounter, mReferenceLock);
            mOpenZipReference.set(openZip);

            mZipReferenceCounter.increment();
            return openZip;
        } finally {
            mReferenceLock.unlock();
        }
    }
}
