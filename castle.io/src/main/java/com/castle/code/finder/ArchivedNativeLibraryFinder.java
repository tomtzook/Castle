package com.castle.code.finder;

import com.castle.annotations.ThreadSafe;
import com.castle.code.NativeLibrary;
import com.castle.code.ArchivedNativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.os.Platform;
import com.castle.util.os.System;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;

@ThreadSafe
public class ArchivedNativeLibraryFinder implements NativeLibraryFinder {

    private final Zip mZip;
    private final Platform mTargetPlatform;
    private final Collection<Path> mSearchPaths;
    private final PatternLibraryFinder mLibraryPatternFinder;

    private ArchivedNativeLibraryFinder(Zip zip, Platform targetPlatform, Collection<Path> searchPaths, PatternLibraryFinder libraryPatternFinder) {
        mZip = zip;
        mTargetPlatform = targetPlatform;
        mSearchPaths = searchPaths;
        mLibraryPatternFinder = libraryPatternFinder;
    }

    public ArchivedNativeLibraryFinder(Zip zip, Platform targetPlatform, Collection<Path> searchPaths) {
        this(zip, targetPlatform, searchPaths, new PatternLibraryFinder(targetPlatform));
    }

    public ArchivedNativeLibraryFinder(Zip zip, Platform targetPlatform, Path searchPath) {
        this(zip, targetPlatform, Collections.singleton(searchPath));
    }

    public ArchivedNativeLibraryFinder(Zip zip, Path searchPath) {
        this(zip, System.platform(), searchPath);
    }

    public ArchivedNativeLibraryFinder(Zip zip, Platform targetPlatform) {
        this(zip, targetPlatform, Collections.emptyList());
    }

    public ArchivedNativeLibraryFinder(Zip zip) {
        this(zip, System.platform());
    }

    @Override
    public NativeLibrary find(String name) throws FindException {
        try (OpenZip zip = mZip.open()) {
            Path path;
            if (mSearchPaths.isEmpty()) {
                SearchPath searchPath = new SearchPath(zip.pathFinder(), zip.getRootPaths());
                path = mLibraryPatternFinder.find(name, Collections.singleton(searchPath));
            } else {
                path = mLibraryPatternFinder.findIn(name, mSearchPaths);
            }
            return new ArchivedNativeLibrary(name, mTargetPlatform, mZip, path);
        } catch (IOException e) {
            throw new FindException(e);
        }
    }
}
