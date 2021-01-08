package com.castle.code.finder;

import com.castle.nio.PatternPathFinder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Predicate;
import java.util.regex.Pattern;

public class SearchPath {

    private final PatternPathFinder mPathFinder;
    private final Iterable<Path> mRoots;

    public SearchPath(PatternPathFinder pathFinder, Iterable<Path> roots) {
        mPathFinder = pathFinder;
        mRoots = roots;
    }

    public SearchPath(PatternPathFinder pathFinder) {
        this(pathFinder, null);
    }

    public Path findOne(Pattern pattern, Predicate<BasicFileAttributes> matcher) throws IOException {
        if (mRoots == null) {
            return mPathFinder.findOne(pattern, matcher);
        } else {
            return mPathFinder.findOne(pattern, matcher, mRoots);
        }
    }
}
