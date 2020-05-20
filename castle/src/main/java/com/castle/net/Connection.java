package com.castle.net;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface Connection extends Closeable {

    InputStream inputStream() throws IOException;
    OutputStream outputStream() throws IOException;

    @Override
    void close() throws IOException;
}
