package com.castle.util.closeables;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

public class LockBasedReferenceCounter extends AtomicReferenceCounter {

    private final Lock mLock;

    public LockBasedReferenceCounter(AtomicInteger counter, Lock lock) {
        super(counter);
        mLock = lock;
    }

    @Override
    public void decrement(Closeable closeable) throws IOException {
        mLock.lock();
        try {
            super.decrement(closeable);
        } finally {
            mLock.unlock();
        }
    }
}
