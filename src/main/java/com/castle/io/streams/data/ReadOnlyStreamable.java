package com.castle.io.streams.data;

import java.io.IOException;
import java.io.InputStream;

public interface ReadOnlyStreamable {

    InputStream openRead() throws IOException;
}
