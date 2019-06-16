package com.castle.code;

import com.castle.io.streams.data.StreamableData;
import com.castle.util.os.Architecture;

public interface NativeLibrary extends StreamableData {

    String getName();
    Architecture getTargetArchitecture();
}
