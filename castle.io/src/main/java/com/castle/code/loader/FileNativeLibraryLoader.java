package com.castle.code.loader;

import com.castle.code.FileNativeLibrary;
import com.castle.code.NativeLibrary;
import com.castle.exceptions.CodeLoadException;
import com.castle.util.os.Platform;
import com.castle.util.os.System;

public class FileNativeLibraryLoader implements NativeLibraryLoader {

    private final Platform mCurrentPlatform;

    public FileNativeLibraryLoader(Platform currentPlatform) {
        mCurrentPlatform = currentPlatform;
    }

    public FileNativeLibraryLoader() {
        this(System.platform());
    }

    @Override
    public boolean supports(NativeLibrary nativeLibrary) {
        return nativeLibrary instanceof FileNativeLibrary &&
                nativeLibrary.getTargetArchitecture().equals(mCurrentPlatform);
    }

    @Override
    public void load(NativeLibrary nativeLibrary) throws CodeLoadException {
        if (!supports(nativeLibrary)) {
            throw new IllegalArgumentException("Unsupported library");
        }

        FileNativeLibrary fileNativeLibrary = (FileNativeLibrary) nativeLibrary;
        java.lang.System.load(fileNativeLibrary.getPath().toAbsolutePath().toString());
    }
}
