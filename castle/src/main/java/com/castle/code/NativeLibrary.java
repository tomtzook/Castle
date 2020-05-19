package com.castle.code;

import com.castle.io.streams.data.ReadOnlyStreamable;
import com.castle.util.os.Architecture;

public interface NativeLibrary extends ReadOnlyStreamable {

    String getName();
    Architecture getTargetArchitecture();
}
