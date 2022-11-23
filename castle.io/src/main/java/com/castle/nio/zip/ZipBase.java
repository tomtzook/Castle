package com.castle.nio.zip;

import com.castle.nio.Archive;
import com.castle.util.closeables.AtomicReferenceCounter;
import com.castle.util.closeables.ReferenceCounter;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ZipBase<T extends OpenZip> implements Archive<T> {

    private final ZipOpener<T> mZipOpener;

    private final AtomicReference<T> mOpenZipReference;
    private final Lock mReferenceLock;
    private final ReferenceCounter mZipReferenceCounter;

    public ZipBase(ZipOpener<T> zipOpener) {
        mZipOpener = zipOpener;

        mOpenZipReference = new AtomicReference<>();
        mReferenceLock = new ReentrantLock();
        mZipReferenceCounter = new AtomicReferenceCounter();
    }

    public T open() throws IOException {
        mReferenceLock.lock();
        try {
            T openZip = mOpenZipReference.get();
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
