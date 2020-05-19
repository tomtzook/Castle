package com.castle.nio.zip;

import com.castle.annotations.Immutable;
import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.util.closeables.Closeables;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Immutable
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
        try {
            extractInto(entryPath, tempPath.originalPath());
        } catch (IOException e) {
            Closeables.silentClose(tempPath);
            throw e;
        }

        return tempPath;
    }

    public void extractInto(Path entryPath, Path destination) throws IOException {
        Files.copy(entryPath, destination, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
    }
}
