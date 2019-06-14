package com.castle.nio;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class DelegatingPath implements Path {

    private final Path mPath;

    public DelegatingPath(Path path) {
        mPath = path;
    }

    @Override
    public FileSystem getFileSystem() {
        return mPath.getFileSystem();
    }

    @Override
    public boolean isAbsolute() {
        return mPath.isAbsolute();
    }

    @Override
    public Path getRoot() {
        return mPath.getRoot();
    }

    @Override
    public Path getFileName() {
        return mPath.getFileName();
    }

    @Override
    public Path getParent() {
        return mPath.getParent();
    }

    @Override
    public int getNameCount() {
        return mPath.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return mPath.getName(index);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return mPath.subpath(beginIndex, endIndex);
    }

    @Override
    public boolean startsWith(Path other) {
        return mPath.startsWith(other);
    }

    @Override
    public boolean startsWith(String other) {
        return mPath.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return mPath.endsWith(other);
    }

    @Override
    public boolean endsWith(String other) {
        return mPath.endsWith(other);
    }

    @Override
    public Path normalize() {
        return mPath.normalize();
    }

    @Override
    public Path resolve(Path other) {
        return mPath.resolve(other);
    }

    @Override
    public Path resolve(String other) {
        return mPath.resolve(other);
    }

    @Override
    public Path resolveSibling(Path other) {
        return mPath.resolveSibling(other);
    }

    @Override
    public Path resolveSibling(String other) {
        return mPath.resolveSibling(other);
    }

    @Override
    public Path relativize(Path other) {
        return mPath.relativize(other);
    }

    @Override
    public URI toUri() {
        return mPath.toUri();
    }

    @Override
    public Path toAbsolutePath() {
        return mPath.toAbsolutePath();
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return mPath.toRealPath(options);
    }

    @Override
    public File toFile() {
        return mPath.toFile();
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return mPath.register(watcher, events, modifiers);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return mPath.register(watcher, events);
    }

    @Override
    public Iterator<Path> iterator() {
        return mPath.iterator();
    }

    @Override
    public int compareTo(Path other) {
        return mPath.compareTo(other);
    }
}
