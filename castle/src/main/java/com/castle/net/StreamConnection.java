package com.castle.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface StreamConnection extends Connection {

    InputStream inputStream() throws IOException;
    OutputStream outputStream() throws IOException;

    @Override
    void close() throws IOException;
}
