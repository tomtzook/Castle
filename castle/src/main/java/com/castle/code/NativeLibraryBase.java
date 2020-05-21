package com.castle.code;

import com.castle.annotations.Immutable;
import com.castle.util.os.Architecture;

@Immutable
public abstract class NativeLibraryBase implements NativeLibrary {

    private final String mName;
    private final Architecture mArchitecture;

    protected NativeLibraryBase(String name, Architecture architecture) {
        mName = name;
        mArchitecture = architecture;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Architecture getTargetArchitecture() {
        return mArchitecture;
    }
}
