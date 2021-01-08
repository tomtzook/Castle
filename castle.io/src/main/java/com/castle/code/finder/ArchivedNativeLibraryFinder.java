package com.castle.code.finder;

import com.castle.annotations.ThreadSafe;
import com.castle.code.NativeLibrary;
import com.castle.code.ArchivedNativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.nio.PathMatching;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.os.Platform;
import com.castle.util.os.System;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Pattern;

@ThreadSafe
public class ArchivedNativeLibraryFinder implements NativeLibraryFinder {

    private final Zip mZip;
    private final Path mArchiveBasePath;
    private final Platform mTargetPlatform;

    public ArchivedNativeLibraryFinder(Zip zip, Path archiveBasePath, Platform targetPlatform) {
        mZip = zip;
        mArchiveBasePath = archiveBasePath;
        mTargetPlatform = targetPlatform;
    }

    public ArchivedNativeLibraryFinder(Zip zip, Path archiveBasePath) {
        this(zip, archiveBasePath, System.platform());
    }

    public ArchivedNativeLibraryFinder(Zip zip, Platform platform) {
        this(zip, null, platform);
    }

    public ArchivedNativeLibraryFinder(Zip zip) {
        this(zip, null, System.platform());
    }

    @Override
    public NativeLibrary find(String name) throws FindException {
        try (OpenZip zip = mZip.open()) {
            Pattern filePattern = buildFindPattern(name);

            Path path = findPath(zip, filePattern);
            return new ArchivedNativeLibrary(name, mTargetPlatform, mZip, path);
        } catch (IOException e) {
            throw new FindException(e);
        }
    }

    private Pattern buildFindPattern(String name) {
        if (mArchiveBasePath == null) {
            return Pattern.compile(String.format(".*%s\\/%s\\/.*%s\\.(dll|so|dylib)$",
                    mTargetPlatform.getOperatingSystem().name().toLowerCase(),
                    mTargetPlatform.getArchitecture(),
                    name));
        } else {
            return Pattern.compile(String.format("^%s\\/%s\\/%s\\/.*%s\\.(dll|so|dylib)$",
                    mArchiveBasePath.toString(),
                    mTargetPlatform.getOperatingSystem().name().toLowerCase(),
                    mTargetPlatform.getArchitecture(),
                    name));
        }
    }

    private Path findPath(OpenZip zip, Pattern pattern) throws FindException, IOException {
        Collection<Path> allFound = zip.pathFinder().findAll(pattern, PathMatching.fileMatcher());
        if (allFound.size() != 1) {
            throw new FindException(String.format("Expected to find 1, by found %d paths: %s",
                    allFound.size(), pattern.pattern()));
        }

        return allFound.iterator().next();
    }
}
