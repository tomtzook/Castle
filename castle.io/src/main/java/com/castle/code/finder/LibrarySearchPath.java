package com.castle.code.finder;

import com.castle.code.NativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.util.os.Platform;
import com.castle.util.os.System;

import java.io.IOException;
import java.util.regex.Pattern;

public interface LibrarySearchPath {

    NativeLibrary find(Platform targetPlatform, String name) throws IOException, FindException;
    NativeLibrary find(Platform targetPlatform, Pattern pattern) throws IOException, FindException;

    default NativeLibrary find(String name) throws IOException, FindException {
        return find(System.platform(), name);
    }

    default NativeLibrary find(Pattern pattern) throws IOException, FindException {
        return find(System.platform(), pattern);
    }
}
