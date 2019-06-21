package com.castle.nio.zip;

import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class ZipEntryExtractor {

    private final TempPathGenerator mPathGenerator;

    public ZipEntryExtractor(TempPathGenerator pathGenerator) {
        mPathGenerator = pathGenerator;
    }

    public ZipEntryExtractor() {
        this(new TempPathGenerator("", "zipEntry"));
    }

    public TempPath extract(Path entryPath) throws IOException {
        TempPath tempPath = mPathGenerator.generateFile();
        extractInto(entryPath, tempPath.originalPath());

        return tempPath;
    }

    public void extractInto(Path entryPath, Path destination) throws IOException {
        if (!entryPath.getFileSystem().isOpen()) {
            throw new IOException("Zip filesystem is closed");
        }

        Files.copy(entryPath, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }
}
