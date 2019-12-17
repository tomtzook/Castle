package com.castle.util.closeables;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

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
