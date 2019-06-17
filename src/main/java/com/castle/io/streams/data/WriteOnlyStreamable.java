package com.castle.io.streams.data;

import java.io.IOException;
import java.io.OutputStream;

public interface WriteOnlyStreamable {

    OutputStream openWrite() throws IOException;
}
