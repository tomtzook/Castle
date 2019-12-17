package com.castle.util.closeables;

import java.io.Closeable;
import java.io.IOException;

public abstract class ReferencedCloseable implements Closeable {

    private final ReferenceCounter mReferenceCounter;

    public ReferencedCloseable(ReferenceCounter referenceCounter) {
        mReferenceCounter = referenceCounter;
    }

    @Override
    public void close() throws IOException {
        mReferenceCounter.decrement(this::doClose);
    }

    protected abstract void doClose() throws IOException;
}
