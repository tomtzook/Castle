package com.castle.nio.zip;

import com.castle.io.streams.data.zip.OpenZipPathStreamable;
import com.castle.io.streams.data.ReadOnlyStreamable;
import com.castle.nio.PathMatching;
import com.castle.nio.PatternPathFinder;
import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;

public class OpenZip implements Closeable {

    private final FileSystem mFileSystem;
    private final ZipEntryExtractor mEntryExtractor;
    private final PatternPathFinder mPathFinder;
    private final AtomicInteger mUsageReferences;
    private final Object mReferencesMutex;

    public OpenZip(FileSystem zipFileSystem, ZipEntryExtractor entryExtractor, PatternPathFinder pathFinder, AtomicInteger usageReferences, Object referencesMutex) {
        mFileSystem = zipFileSystem;
        mEntryExtractor = entryExtractor;
        mPathFinder = pathFinder;
        mUsageReferences = usageReferences;
        mReferencesMutex = referencesMutex;
    }

    public OpenZip(FileSystem zipFileSystem, TempPathGenerator pathGenerator, AtomicInteger usageReferences, Object referencesMutex) {
        this(zipFileSystem, new ZipEntryExtractor(pathGenerator), new PatternPathFinder(zipFileSystem), usageReferences, referencesMutex);
    }

    public OpenZip(FileSystem zipFileSystem, AtomicInteger usageReferences, Object referencesMutex) {
        this(zipFileSystem, new TempPathGenerator("zip", "generated"), usageReferences, referencesMutex);
    }

    public boolean isOpen() {
        return mFileSystem.isOpen();
    }

    public PatternPathFinder pathFinder() {
        return mPathFinder;
    }

    public ZipEntryExtractor entryExtractor() {
        return mEntryExtractor;
    }

    public Path getPath(String first, String... more) {
        return mFileSystem.getPath(first, more);
    }

    public ReadOnlyStreamable getPathData(Path path) {
        if (!Files.isRegularFile(path)) {
            throw new IllegalArgumentException("Cannot stream data of non-file");
        }

        return new OpenZipPathStreamable(mEntryExtractor, path);
    }

    public Path findFile(Pattern pattern) throws IOException {
        return mPathFinder.findOne(pattern, PathMatching.fileMatcher());
    }

    public Path find(Pattern pattern) throws IOException {
        return mPathFinder.findOne(pattern);
    }

    public Collection<Path> findAllFiles(Pattern pattern) throws IOException {
        return mPathFinder.findAll(pattern, PathMatching.fileMatcher());
    }

    public Collection<Path> findAll(Pattern pattern) throws IOException {
        return mPathFinder.findAll(pattern);
    }

    public TempPath extract(Path entryPath) throws IOException {
        return mEntryExtractor.extract(entryPath);
    }

    public void extractInto(Path entryPath, Path destination) throws IOException {
        mEntryExtractor.extractInto(entryPath, destination);
    }

    @Override
    public void close() throws IOException {
        synchronized (mReferencesMutex) {
            if (mUsageReferences.decrementAndGet() <= 0) {
                mFileSystem.close();
            }
        }
    }
}
