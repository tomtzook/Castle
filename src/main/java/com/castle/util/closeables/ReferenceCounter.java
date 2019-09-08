package com.castle.util.closeables;

import java.io.Closeable;
import java.io.IOException;

public interface ReferenceCounter {

    void decrement(Closeable closeable) throws IOException;
}
