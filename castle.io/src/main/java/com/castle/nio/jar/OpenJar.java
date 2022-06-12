package com.castle.nio.jar;

import com.castle.nio.PatternPathFinder;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.ZipEntryExtractor;
import com.castle.util.closeables.ReferenceCounter;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.concurrent.locks.Lock;

public class OpenJar extends OpenZip {

    public OpenJar(FileSystem zipFileSystem, ZipEntryExtractor entryExtractor, PatternPathFinder pathFinder, ReferenceCounter referenceCounter, Lock closeLock) {
        super(zipFileSystem, entryExtractor, pathFinder, referenceCounter, closeLock);
    }

    public OpenJar(FileSystem zipFileSystem, TempPathGenerator pathGenerator, ReferenceCounter referenceCounter, Lock closeLock) {
        super(zipFileSystem, pathGenerator, referenceCounter, closeLock);
    }

    public OpenJar(FileSystem zipFileSystem, ReferenceCounter referenceCounter, Lock closeLock) {
        super(zipFileSystem, referenceCounter, closeLock);
    }

    public JarManifest manifest() throws IOException {
        return new JarManifest(getPath("META-INF", "MANIFEST.MF"));
    }
}
