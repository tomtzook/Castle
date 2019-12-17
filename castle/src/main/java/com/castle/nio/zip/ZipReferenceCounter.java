package com.castle.nio.zip;

import com.castle.annotations.ThreadSafe;
import com.castle.util.closeables.LockBasedReferenceCounter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;

@ThreadSafe
public class ZipReferenceCounter extends LockBasedReferenceCounter {

    private final AtomicInteger mReferencesCount;

    private ZipReferenceCounter(Lock lock, AtomicInteger referencesCount) {
        super(referencesCount, lock);

        mReferencesCount = referencesCount;
    }

    public ZipReferenceCounter(Lock lock) {
        this(lock, new AtomicInteger(0));
    }

    public void incrementReferencesCount() {
        mReferencesCount.incrementAndGet();
    }
}
