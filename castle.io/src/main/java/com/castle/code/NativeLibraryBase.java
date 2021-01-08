package com.castle.code;

import com.castle.annotations.Immutable;
import com.castle.util.os.Platform;

@Immutable
public abstract class NativeLibraryBase implements NativeLibrary {

    private final String mName;
    private final Platform mPlatform;

    protected NativeLibraryBase(String name, Platform platform) {
        mName = name;
        mPlatform = platform;
    }

    @Override
    public String getName() {
        return mName;
    }

    @Override
    public Platform getTargetArchitecture() {
        return mPlatform;
    }
}
