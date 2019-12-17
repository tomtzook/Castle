package com.castle.util.closeables;

import com.castle.annotations.ThreadSafe;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class AtomicReferenceCounter implements ReferenceCounter {

    private final AtomicInteger mCounter;

    public AtomicReferenceCounter(AtomicInteger counter) {
        mCounter = counter;
    }

    @Override
    public void decrement(Closeable closeable) throws IOException {
        if (mCounter.decrementAndGet() == 0) {
            closeable.close();
        }
    }
}
