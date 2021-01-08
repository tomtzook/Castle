package com.castle.code.finder;

import com.castle.code.NativeLibrary;
import com.castle.exceptions.FindException;

public interface NativeLibraryFinder {

    NativeLibrary find(String name) throws FindException;
}
