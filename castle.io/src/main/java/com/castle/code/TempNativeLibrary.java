package com.castle.code;

import com.castle.annotations.Stateless;
import com.castle.nio.temp.TempPath;
import com.castle.io.streams.TempPathInputStream;
import com.castle.util.os.Architecture;

import java.io.IOException;
import java.io.InputStream;

@Stateless
public abstract class TempNativeLibrary extends NativeLibraryBase {

    protected TempNativeLibrary(String name, Architecture architecture) {
        super(name, architecture);
    }

    @Override
    public InputStream openRead() throws IOException {
        return new TempPathInputStream(makeTempFile());
    }

    public abstract TempPath makeTempFile() throws IOException;
}
