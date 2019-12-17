package com.castle.nio;

import com.castle.annotations.Immutable;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Immutable
public class PatternPathFinder extends PathFinder {

    public PatternPathFinder(FileSystem fileSystem) {
        super(fileSystem);
    }

    public Path findOne(Pattern pattern) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return findOne(PathMatching.pathMatcher(pathMatcher));
    }

    public Path findOne(Pattern pattern, Predicate<BasicFileAttributes> fileAttributesPredicate) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return findOne(PathMatching.pathMatcher(pathMatcher, fileAttributesPredicate));
    }

    public Path findOne(Pattern pattern, Predicate<BasicFileAttributes> fileAttributesPredicate, Path root) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return findOne(PathMatching.pathMatcher(pathMatcher, fileAttributesPredicate), root);
    }

    public Collection<Path> findAll(Pattern pattern) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return findAll(PathMatching.pathMatcher(pathMatcher));
    }

    public Collection<Path> findAll(Pattern pattern, Predicate<BasicFileAttributes> fileAttributesPredicate) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return findAll(PathMatching.pathMatcher(pathMatcher, fileAttributesPredicate));
    }

    public Collection<Path> findAll(Pattern pattern, Predicate<BasicFileAttributes> fileAttributesPredicate, Path root) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return findAll(PathMatching.pathMatcher(pathMatcher, fileAttributesPredicate), root);
    }

    public Stream<Path> find(Pattern pattern) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return find(PathMatching.pathMatcher(pathMatcher));
    }

    public Stream<Path> find(Pattern pattern, Predicate<BasicFileAttributes> fileAttributesPredicate) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return find(PathMatching.pathMatcher(pathMatcher, fileAttributesPredicate));
    }

    public Stream<Path> find(Pattern pattern, Predicate<BasicFileAttributes> fileAttributesPredicate, Path root) throws IOException {
        PathMatcher pathMatcher = getPathMatcher(pattern);
        return find(PathMatching.pathMatcher(pathMatcher, fileAttributesPredicate), root);
    }

    private PathMatcher getPathMatcher(Pattern pattern) {
        return mFileSystem.getPathMatcher(String.format("regex:%s", pattern.pattern()));
    }
}
