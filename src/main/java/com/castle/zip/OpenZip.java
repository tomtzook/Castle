package com.castle.zip;

import com.castle.io.streams.data.zip.LazyExtractZipData;
import com.castle.io.streams.data.StreamableData;
import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.regex.Pattern;

public class OpenZip implements Closeable {

    private final FileSystem mFileSystem;
    private final ZipEntryExtractor mEntryExtractor;
    private final ZipEntryFinder mEntryFinder;

    public OpenZip(FileSystem zipFileSystem, ZipEntryExtractor entryExtractor, ZipEntryFinder entryFinder) {
        mFileSystem = zipFileSystem;
        mEntryExtractor = entryExtractor;
        mEntryFinder = entryFinder;
    }

    public OpenZip(FileSystem zipFileSystem, TempPathGenerator pathGenerator) {
        this(zipFileSystem, new ZipEntryExtractor(pathGenerator), new ZipEntryFinder(zipFileSystem));
    }

    public OpenZip(FileSystem zipFileSystem) {
        this(zipFileSystem, new TempPathGenerator("zip", "generated"));
    }

    public boolean isOpen() {
        return mFileSystem.isOpen();
    }

    public Path getPath(String first, String... more) {
        return mFileSystem.getPath(first, more);
    }

    public StreamableData getData(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Cannot stream data of non-file");
        }

        return new LazyExtractZipData(mEntryExtractor, path);
    }

    public Path findFile(Pattern pattern) throws IOException {
        return mEntryFinder.findFile(pattern);
    }

    public Path find(Pattern pattern) throws IOException {
        return mEntryFinder.find(pattern);
    }

    public Collection<Path> findAllFiles(Pattern pattern) throws IOException {
        return mEntryFinder.findAllFiles(pattern);
    }

    public Collection<Path> findAll(Pattern pattern) throws IOException {
        return mEntryFinder.findAll(pattern);
    }

    public TempPath extract(Path entryPath) throws IOException {
        return mEntryExtractor.extract(entryPath);
    }

    public void extractInto(Path entryPath, Path destination) throws IOException {
        mEntryExtractor.extractInto(entryPath, destination);
    }

    @Override
    public void close() throws IOException {
        mFileSystem.close();
    }
}
