package com.castle.zip;

import com.castle.nio.PathFinder;
import com.castle.nio.PathMatching;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Collection;
import java.util.regex.Pattern;

public class ZipEntryFinder {

    private final FileSystem mZipFileSystem;
    private final PathFinder mPathFinder;

    public ZipEntryFinder(FileSystem zipFileSystem, PathFinder pathFinder) {
        mZipFileSystem = zipFileSystem;
        mPathFinder = pathFinder;
    }

    public ZipEntryFinder(FileSystem zipFileSystem) {
        this(zipFileSystem, new PathFinder(zipFileSystem));
    }

    public Path findFile(Pattern pattern) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return mPathFinder.findOne(PathMatching.fileMatcher(pathMatcher));
    }

    public Path find(Pattern pattern) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return mPathFinder.findOne(PathMatching.pathMatcher(pathMatcher));
    }

    public Collection<Path> findAllFiles(Pattern pattern) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return mPathFinder.findAll(PathMatching.fileMatcher(pathMatcher));
    }

    public Collection<Path> findAll(Pattern pattern) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return mPathFinder.findAll(PathMatching.pathMatcher(pathMatcher));
    }

    private PathMatcher getPathMatcher(Pattern pattern) {
        return mZipFileSystem.getPathMatcher(String.format("regex:%s", pattern.pattern()));
    }
}
