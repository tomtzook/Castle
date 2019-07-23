package com.castle.nio.zip;

import java.nio.file.FileSystem;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenZipFactory {

    public OpenZip create(FileSystem zipFileSystem, AtomicInteger usageReferences, Object referencesMutex) {
        return new OpenZip(zipFileSystem, usageReferences, referencesMutex);
    }
}
