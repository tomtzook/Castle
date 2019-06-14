package com.castle.zip;

import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class ZipEntryExtractor {

    private final TempPathGenerator mPathGenerator;
    private final ZipEntryFinder mZipEntryFinder;

    public ZipEntryExtractor(TempPathGenerator pathGenerator, ZipEntryFinder zipEntryFinder) {
        mPathGenerator = pathGenerator;
        mZipEntryFinder = zipEntryFinder;
    }

    public TempPath extract(Pattern pattern) throws IOException {
        Path entryPath = mZipEntryFinder.findFile(pattern);
        return extract(entryPath);
    }

    public void extractInto(Pattern pattern, Path destination) throws IOException {
        Path entryPath = mZipEntryFinder.findFile(pattern);
        extractInto(entryPath, destination);
    }

    public TempPath extract(Path entryPath) throws IOException {
        TempPath tempPath = mPathGenerator.generateFile();
        extractInto(entryPath, tempPath.originalPath());

        return tempPath;
    }

    public void extractInto(Path entryPath, Path destination) throws IOException {
        Files.copy(entryPath, destination);
    }
}
