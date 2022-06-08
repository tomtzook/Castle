package com.castle.nio.zip;

import com.castle.annotations.ThreadSafe;
import com.castle.nio.Providers;

import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;

@ThreadSafe
public class Zip extends ZipBase<OpenZip> {

    public Zip(ZipOpener<OpenZip> zipOpener) {
        super(zipOpener);
    }

    public static Zip fromPath(FileSystemProvider zipFileSystemProvider, Map<String, ?> fileSystemEnv, Path zipPath) {
        return new Zip(new PathBasedZipOpener<>(zipFileSystemProvider, fileSystemEnv, zipPath, OpenZip::new));
    }

    public static Zip fromPath(Path zipPath) {
        return fromPath(Providers.zipProvider(), new HashMap<>(), zipPath);
    }
}
