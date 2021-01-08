package com.castle.code.loader;

import com.castle.code.FileNativeLibrary;
import com.castle.code.NativeLibrary;
import com.castle.exceptions.CodeLoadException;

public class FileNativeLibraryLoader implements NativeLibraryLoader {

    @Override
    public boolean supports(NativeLibrary nativeLibrary) {
        return nativeLibrary instanceof FileNativeLibrary;
    }

    @Override
    public void load(NativeLibrary nativeLibrary) throws CodeLoadException {
        if (!(nativeLibrary instanceof FileNativeLibrary)) {
            throw new IllegalArgumentException("Unsupported type. Expected FileNativeLibrary");
        }

        FileNativeLibrary fileNativeLibrary = (FileNativeLibrary) nativeLibrary;
        System.load(fileNativeLibrary.getPath().toAbsolutePath().toString());
    }
}
