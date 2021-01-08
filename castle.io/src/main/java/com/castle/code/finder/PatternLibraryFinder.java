package com.castle.code.finder;

import com.castle.exceptions.FindException;
import com.castle.nio.PathMatching;
import com.castle.nio.PatternPathFinder;
import com.castle.nio.exceptions.PathMatchingException;
import com.castle.util.os.Platform;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class PatternLibraryFinder {

    private final Platform mTargetPlatform;

    public PatternLibraryFinder(Platform targetPlatform) {
        mTargetPlatform = targetPlatform;
    }

    public Path findIn(String name, Iterable<Path> paths) throws FindException {
        Iterable<SearchPath> searchPaths = splitRootsByFileSystem(paths);
        return find(name, searchPaths);
    }

    public Path find(String name, Iterable<SearchPath> searchPaths) throws FindException {
        try {
            Pattern filePattern = buildFindPattern(name);
            return findPath(searchPaths, filePattern);
        } catch (IOException e) {
            throw new FindException(e);
        }
    }

    private Iterable<SearchPath> splitRootsByFileSystem(Iterable<Path> paths) {
        return StreamSupport.stream(paths.spliterator(), false)
                .collect(Collectors.groupingBy(Path::getFileSystem))
                .entrySet().stream()
                .map((entry)-> new SearchPath(new PatternPathFinder(entry.getKey()), entry.getValue()))
                .collect(Collectors.toList());
    }

    private Pattern buildFindPattern(String name) {
        if (mTargetPlatform == null) {
            return Pattern.compile(String.format(".*%s\\.(dll|so|dylib)$",
                    name));
        } else {
            return Pattern.compile(String.format(".*%s\\/%s\\/.*%s\\.(dll|so|dylib)$",
                    mTargetPlatform.getOperatingSystem().name().toLowerCase(),
                    mTargetPlatform.getArchitecture().name().toLowerCase(),
                    name));
        }
    }

    private Path findPath(Iterable<SearchPath> searchPaths, Pattern pattern) throws FindException, IOException {
        Predicate<BasicFileAttributes> matcher = PathMatching.fileMatcher();
        for (SearchPath searchPath : searchPaths) {
            //noinspection CatchMayIgnoreException
            try {
                return searchPath.findOne(pattern, matcher);
            } catch (PathMatchingException e) { }
        }

        throw new FindException(pattern.pattern() + " not found");
    }
}
