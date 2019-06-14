package com.castle.io.streams.data;

import java.io.IOException;
import java.io.InputStream;

public interface StreamableData {

    InputStream openRead() throws IOException;
}
