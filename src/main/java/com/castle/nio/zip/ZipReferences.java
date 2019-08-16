package com.castle.nio.zip;

import java.io.Closeable;
import java.io.IOException;

public class ZipReferences {

    private final Object mMutex;
    private int mReferencesCount;

    public ZipReferences(Object mutex) {
        mMutex = mutex;
        mReferencesCount = 0;
    }

    public void incrementReferencesCount() {
        mReferencesCount++;
    }

    public void closeReference(Closeable closeable) throws IOException {
        synchronized (mMutex) {
            if ((--mReferencesCount) <= 0) {
                closeable.close();
            }
        }
    }
}
