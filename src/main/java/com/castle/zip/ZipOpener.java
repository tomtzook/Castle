package com.castle.zip;

import java.nio.file.FileSystem;

public class ZipOpener {

    public OpenZip open(FileSystem zipFileSystem) {
        return new OpenZip(zipFileSystem);
    }
}
