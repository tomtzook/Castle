package com.castle.nio;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.function.LongPredicate;

public class PathMatching {

    private PathMatching() {}

    public static BiPredicate<Path, BasicFileAttributes> fileMatcher(PathMatcher pathMatcher) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path) && basicFileAttributes.isRegularFile();
    }

    public static BiPredicate<Path, BasicFileAttributes> directoryMatcher(PathMatcher pathMatcher) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path) && basicFileAttributes.isDirectory();
    }

    public static BiPredicate<Path, BasicFileAttributes> sizeMatcher(PathMatcher pathMatcher, LongPredicate sizePredicate) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path) && sizePredicate.test(basicFileAttributes.size());
    }
}
