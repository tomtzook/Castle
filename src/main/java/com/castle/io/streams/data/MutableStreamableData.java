package com.castle.io.streams.data;

import java.io.IOException;
import java.io.OutputStream;

public interface MutableStreamableData extends StreamableData {

    OutputStream openWrite() throws IOException;
}
