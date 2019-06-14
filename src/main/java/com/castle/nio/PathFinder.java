package com.castle.nio;

import com.castle.util.throwables.Throwables;

import java.io.IOException;
import java.nio.file.AccessMode;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.AccessException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {

    private static final int MAX_DEPTH = 10;

    private final FileSystem mFileSystem;

    public PathFinder(FileSystem fileSystem) {
        mFileSystem = fileSystem;
    }

    public Path findOne(BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        return findOne(matcher, MAX_DEPTH, options);
    }

    public Path findOne(BiPredicate<Path, BasicFileAttributes> matcher, Path root, FileVisitOption... options) throws IOException {
        return findOne(matcher, root, MAX_DEPTH, options);
    }

    public Path findOne(BiPredicate<Path, BasicFileAttributes> matcher, Iterable<Path> roots, FileVisitOption... options) throws IOException {
        return findOne(matcher, roots, MAX_DEPTH, options);
    }

    public Path findOne(BiPredicate<Path, BasicFileAttributes> matcher, int maxDepth, FileVisitOption... options) throws IOException {
        return findOne(matcher, mFileSystem.getRootDirectories(), maxDepth, options);
    }

    public Path findOne(BiPredicate<Path, BasicFileAttributes> matcher, Path root, int maxDepth, FileVisitOption... options) throws IOException {
        return findOne(matcher, Collections.singleton(root), maxDepth, options);
    }

    public Path findOne(BiPredicate<Path, BasicFileAttributes> matcher, Iterable<Path> roots, int maxDepth, FileVisitOption... options) throws IOException {
        try (Stream<Path> stream = find(matcher, roots, maxDepth, options)) {
            if (stream.count() != 1) {
                throw new IOException("Only 1 path wanted. Found: " + stream.count());
            }

            //noinspection OptionalGetWithoutIsPresent
            return stream.findAny().get();
        }
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        return findAll(matcher, MAX_DEPTH, options);
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, Path root, FileVisitOption... options) throws IOException {
        return findAll(matcher, root, MAX_DEPTH, options);
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, Iterable<Path> roots, FileVisitOption... options) throws IOException {
        return findAll(matcher, roots, MAX_DEPTH, options);
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, int maxDepth, FileVisitOption... options) throws IOException {
        return findAll(matcher, mFileSystem.getRootDirectories(), maxDepth, options);
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, Path root, int maxDepth, FileVisitOption... options) throws IOException {
        return findAll(matcher, Collections.singleton(root), maxDepth, options);
    }

    public Collection<Path> findAll(BiPredicate<Path, BasicFileAttributes> matcher, Iterable<Path> roots, int maxDepth, FileVisitOption... options) throws IOException {
        try (Stream<Path> stream = find(matcher, roots, maxDepth, options)) {
            return stream.collect(Collectors.toList());
        }
    }

    public Stream<Path> find(BiPredicate<Path, BasicFileAttributes> matcher, FileVisitOption... options) throws IOException {
        return find(matcher, mFileSystem.getRootDirectories(), MAX_DEPTH, options);
    }

    public Stream<Path> find(BiPredicate<Path, BasicFileAttributes> matcher, int maxDepth, FileVisitOption... options) throws IOException {
        return find(matcher, mFileSystem.getRootDirectories(), maxDepth, options);
    }

    public Stream<Path> find(BiPredicate<Path, BasicFileAttributes> matcher, Iterable<Path> roots, FileVisitOption... options) throws IOException {
        return find(matcher, roots, MAX_DEPTH, options);
    }

    public Stream<Path> find(BiPredicate<Path, BasicFileAttributes> matcher, Iterable<Path> roots, int maxDepth, FileVisitOption... options) throws IOException {
        Stream.Builder<Stream<Path>> builder = Stream.builder();
        Collection<AutoCloseable> closeables = new ArrayList<>();

        for (Path root : roots) {
            Stream<Path> stream = find(matcher, root, maxDepth, options);
            closeables.add(stream);

            builder.add(stream);
        }

        return builder.build()
                .onClose(()->closeables.forEach(Throwables.silentCloser()))
                .flatMap(i -> i);
    }

    public Stream<Path> find(BiPredicate<Path, BasicFileAttributes> matcher, Path root, FileVisitOption... options) throws IOException {
        return find(matcher, root, MAX_DEPTH, options);
    }

    public Stream<Path> find(BiPredicate<Path, BasicFileAttributes> matcher, Path root, int maxDepth, FileVisitOption... options) throws IOException {
        try {
            mFileSystem.provider().checkAccess(root, AccessMode.READ);
            return Files.find(root, maxDepth, matcher, options);
        } catch (AccessException e) {
            return Stream.empty();
        }
    }
}
