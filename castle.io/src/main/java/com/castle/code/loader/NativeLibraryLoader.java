package com.castle.code.loader;

import com.castle.code.NativeLibrary;
import com.castle.exceptions.CodeLoadException;

public interface NativeLibraryLoader {

    boolean supports(NativeLibrary nativeLibrary);
    void load(NativeLibrary nativeLibrary) throws CodeLoadException;
}
