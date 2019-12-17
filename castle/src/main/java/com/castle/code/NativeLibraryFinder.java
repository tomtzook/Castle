package com.castle.code;

import com.castle.exceptions.FindException;

public interface NativeLibraryFinder {

    NativeLibrary find(String name) throws FindException;
}
