package com.castle.code;

import com.castle.annotations.Stateless;
import com.castle.nio.temp.TempPath;
import com.castle.io.streams.TempPathInputStream;
import com.castle.util.os.Platform;

import java.io.IOException;
import java.io.InputStream;

@Stateless
public abstract class TempNativeLibrary extends NativeLibraryBase {

    protected TempNativeLibrary(String name, Platform platform) {
        super(name, platform);
    }

    @Override
    public InputStream openRead() throws IOException {
        return new TempPathInputStream(makeTempFile());
    }

    public abstract TempPath makeTempFile() throws IOException;
}
