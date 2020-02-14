package com.castle.util.closeables;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

public abstract class ReferencedCloseable implements Closeable {

    private final ReferenceCounter mReferenceCounter;
    private final Lock mLock;

    public ReferencedCloseable(ReferenceCounter referenceCounter, Lock lock) {
        mReferenceCounter = referenceCounter;
        mLock = lock;
    }

    @Override
    public void close() throws IOException {
        mLock.lock();
        try {
            if (mReferenceCounter.decrement()) {
                doClose();
            }
        } finally {
            mLock.unlock();
        }
    }

    protected abstract void doClose() throws IOException;
}
