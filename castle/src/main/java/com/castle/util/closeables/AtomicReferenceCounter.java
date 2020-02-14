package com.castle.util.closeables;

import com.castle.annotations.ThreadSafe;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

@ThreadSafe
public class AtomicReferenceCounter implements ReferenceCounter {

    private final AtomicInteger mCounter;

    public AtomicReferenceCounter(AtomicInteger counter) {
        mCounter = counter;
    }

    public AtomicReferenceCounter() {
        this(new AtomicInteger(0));
    }

    @Override
    public void increment() {
        mCounter.incrementAndGet();
    }

    @Override
    public boolean decrement() {
        int count = mCounter.decrementAndGet();
        if (count < 0) {
            throw new IllegalStateException("no more references");
        }

        return count == 0;
    }
}
