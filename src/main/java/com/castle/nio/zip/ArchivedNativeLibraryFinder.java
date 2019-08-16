package com.castle.nio.zip;

import com.castle.code.ArchivedNativeLibrary;
import com.castle.code.NativeLibrary;
import com.castle.code.NativeLibraryFinder;
import com.castle.exceptions.FindException;
import com.castle.nio.PathMatching;
import com.castle.util.os.Architecture;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Pattern;

public class ArchivedNativeLibraryFinder implements NativeLibraryFinder {

    private final Zip mZip;
    private final Path mArchiveBasePath;
    private final Architecture mTargetArchitecture;

    public ArchivedNativeLibraryFinder(Zip zip, Path archiveBasePath, Architecture targetArchitecture) {
        mZip = zip;
        mArchiveBasePath = archiveBasePath;
        mTargetArchitecture = targetArchitecture;
    }

    @Override
    public NativeLibrary find(String name) throws FindException {
        try (OpenZip zip = mZip.open()) {
            Pattern filePattern = buildFindPattern(name);

            Path path = findPath(zip, filePattern);
            return new ArchivedNativeLibrary(name, mTargetArchitecture, mZip, path);
        } catch (IOException e) {
            throw new FindException(e);
        }
    }

    private Pattern buildFindPattern(String name) {
        return Pattern.compile(String.format("^%s\\/%s\\/.*%s(dll|so|dylib)$",
                mArchiveBasePath.toString(),
                mTargetArchitecture.getName(),
                name));
    }

    private Path findPath(OpenZip zip, Pattern pattern) throws FindException, IOException {
        Collection<Path> allFound = zip.pathFinder().findAll(pattern, PathMatching.fileMatcher());
        if (allFound.size() != 1) {
            throw new FindException("Expected to find 1, by found several paths");
        }

        return allFound.iterator().next();
    }
}
