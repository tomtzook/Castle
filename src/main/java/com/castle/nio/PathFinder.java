package com.castle.nio;

import com.castle.util.Throwables;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {

    private static final int MAX_DEPTH = 10;

    private final FileSystem mFileSystem;

    public PathFinder(FileSystem fileSystem) {
        mFileSystem = fileSystem;
    }

    public Optional<Path> findFirst(PathMatcher matcher, FileVisitOption... options) throws IOException {
        return findFirst(matcher, MAX_DEPTH, options);
    }

    public Optional<Path> findFirst(PathMatcher matcher, int maxDepth, FileVisitOption... options) throws IOException {
        return findFirst(predicateFromPathMatcher(matcher), maxDepth, options);
    }

    public Optional<Path> findFirst(BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        return findFirst(matcher, MAX_DEPTH, options);
    }

    public Optional<Path> findFirst(BiPredicate<Path, BasicFileAttributes> matcher, int maxDepth, FileVisitOption... options) throws IOException {
        try (Stream<Path> stream = findInFileSystem(maxDepth, matcher, options)) {
            return stream.findFirst();
        }
    }

    public Collection<Path> findAll(PathMatcher matcher, FileVisitOption... options) throws IOException {
        return findAll(matcher, MAX_DEPTH, options);
    }

    public Collection<Path> findAll(PathMatcher matcher, int maxDepth, FileVisitOption... options) throws IOException {
        return findAll(predicateFromPathMatcher(matcher), maxDepth, options);
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        return findAll(matcher, MAX_DEPTH, options);
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, int maxDepth, FileVisitOption... options) throws IOException {
        try (Stream<Path> stream = findInFileSystem(maxDepth, matcher, options)) {
            return stream.collect(Collectors.toList());
        }
    }

    private Stream<Path> findInFileSystem(int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        Stream.Builder<Stream<Path>> builder = Stream.builder();
        Collection<AutoCloseable> closeables = new ArrayList<>();

        for (Path root : mFileSystem.getRootDirectories()) {
            Stream<Path> stream = findInRoot(root, maxDepth, matcher, options);
            closeables.add(stream);

            builder.add(stream);
        }

        return builder.build()
                .onClose(()->closeables.forEach(Throwables.silentCloser()))
                .flatMap(i -> i);
    }

    private Stream<Path> findInRoot(Path root, int maxDepth, BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        return Files.find(root, maxDepth, matcher, options);
    }

    private BiPredicate<Path, BasicFileAttributes> predicateFromPathMatcher(PathMatcher pathMatcher) {
        return (path, attributes) -> pathMatcher.matches(path);
    }
}
