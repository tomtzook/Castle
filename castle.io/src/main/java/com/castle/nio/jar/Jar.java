package com.castle.nio.jar;

import com.castle.nio.Providers;
import com.castle.nio.zip.PathBasedZipOpener;
import com.castle.nio.zip.ZipBase;
import com.castle.nio.zip.ZipOpener;

import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.Map;

public class Jar extends ZipBase<OpenJar> {

    public Jar(ZipOpener<OpenJar> zipOpener) {
        super(zipOpener);
    }

    public static Jar fromPath(FileSystemProvider zipFileSystemProvider, Map<String, ?> fileSystemEnv, Path zipPath) {
        return new Jar(new PathBasedZipOpener<>(zipFileSystemProvider, fileSystemEnv, zipPath, OpenJar::new));
    }

    public static Jar fromPath(Path zipPath) {
        return fromPath(Providers.zipProvider(), new HashMap<>(), zipPath);
    }
}
