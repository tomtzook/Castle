package com.castle.nio;

import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.BiPredicate;
import java.util.function.LongPredicate;
import java.util.function.Predicate;

public class PathMatching {

    private PathMatching() {}

    public static BiPredicate<Path, BasicFileAttributes> pathMatcher(PathMatcher pathMatcher) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path);
    }

    public static BiPredicate<Path, BasicFileAttributes> pathMatcher(PathMatcher pathMatcher, Predicate<BasicFileAttributes> fileAttributesPredicate) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path) && fileAttributesPredicate.test(basicFileAttributes);
    }

    public static Predicate<BasicFileAttributes> fileMatcher() {
        return BasicFileAttributes::isRegularFile;
    }

    public static BiPredicate<Path, BasicFileAttributes> fileMatcher(PathMatcher pathMatcher) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path) && basicFileAttributes.isRegularFile();
    }

    public static Predicate<BasicFileAttributes> directoryMatcher() {
        return BasicFileAttributes::isDirectory;
    }

    public static BiPredicate<Path, BasicFileAttributes> directoryMatcher(PathMatcher pathMatcher) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path) && basicFileAttributes.isDirectory();
    }

    public static BiPredicate<Path, BasicFileAttributes> sizeMatcher(LongPredicate sizePredicate) {
        return (path, basicFileAttributes) -> sizePredicate.test(basicFileAttributes.size());
    }

    public static BiPredicate<Path, BasicFileAttributes> sizeMatcher(PathMatcher pathMatcher, LongPredicate sizePredicate) {
        return (path, basicFileAttributes) -> pathMatcher.matches(path) && sizePredicate.test(basicFileAttributes.size());
    }

    public static Predicate<BasicFileAttributes> allMatcher() {
        return (basicFileAttributes)->true;
    }
}
