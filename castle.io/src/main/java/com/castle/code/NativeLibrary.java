package com.castle.code;

import com.castle.io.streams.data.ReadOnlyStreamable;
import com.castle.util.os.Platform;

public interface NativeLibrary extends ReadOnlyStreamable {

    String getName();
    Platform getTargetArchitecture();
}
