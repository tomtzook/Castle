package com.castle.io.streams;

import java.io.IOException;
import java.io.InputStream;

public class DelegatingInputStream extends InputStream {

    private final InputStream mOriginal;

    public DelegatingInputStream(InputStream original) {
        mOriginal = original;
    }

    @Override
    public int read() throws IOException {
        return mOriginal.read();
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return mOriginal.read(b, off, len);
    }

    @Override
    public int read(byte[] b) throws IOException {
        return mOriginal.read(b);
    }

    @Override
    public boolean markSupported() {
        return mOriginal.markSupported();
    }

    @Override
    public synchronized void mark(int readlimit) {
        mOriginal.mark(readlimit);
    }

    @Override
    public int available() throws IOException {
        return mOriginal.available();
    }

    @Override
    public long skip(long n) throws IOException {
        return mOriginal.skip(n);
    }

    @Override
    public synchronized void reset() throws IOException {
        mOriginal.reset();
    }

    @Override
    public void close() throws IOException {
        mOriginal.close();
    }
}
