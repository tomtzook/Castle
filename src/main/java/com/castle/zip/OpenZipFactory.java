package com.castle.zip;

import java.nio.file.FileSystem;
import java.util.concurrent.atomic.AtomicInteger;

public class OpenZipFactory {

    public OpenZip create(FileSystem zipFileSystem, AtomicInteger usageReferences) {
        return new OpenZip(zipFileSystem, usageReferences);
    }
}
