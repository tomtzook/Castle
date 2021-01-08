package com.castle.code.finder;

import com.castle.code.FileNativeLibrary;
import com.castle.code.NativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.util.os.Platform;
import com.castle.util.os.System;

import java.nio.file.Path;

public class FileNativeLibraryFinder implements NativeLibraryFinder {

    private final PatternLibraryFinder mPatternFinder;
    private final Platform mPlatform;
    private final Iterable<Path> mSearchPaths;

    private FileNativeLibraryFinder(PatternLibraryFinder patternFinder, Iterable<Path> searchPaths, Platform platform) {
        mPatternFinder = patternFinder;
        mPlatform = platform;
        mSearchPaths = searchPaths;
    }

    public FileNativeLibraryFinder(Iterable<Path> searchPaths, Platform platform, LibraryPatternBuilder libraryPatternBuilder) {
        this(new PatternLibraryFinder(platform, libraryPatternBuilder), searchPaths, platform);
    }

    public FileNativeLibraryFinder(Iterable<Path> searchPaths, Platform platform) {
        this(searchPaths, platform, new DefaultLibraryPatternBuilder());
    }

    public FileNativeLibraryFinder(Iterable<Path> searchPaths) {
        this(searchPaths, System.platform());
    }

    @Override
    public NativeLibrary find(String name) throws FindException {
        Path path = mPatternFinder.findIn(name, mSearchPaths);
        return new FileNativeLibrary(path, mPlatform);
    }
}
