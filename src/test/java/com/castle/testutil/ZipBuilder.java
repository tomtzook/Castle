package com.castle.testutil;

import com.castle.io.streams.Streams;
import com.castle.nio.zip.ZipEntryExtractor;
import com.sun.nio.zipfs.ZipFileSystemProvider;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipBuilder {

    private final FileSystemProvider mFileSystemProvider;
    private final Path mZipPath;

    public ZipBuilder(FileSystemProvider fileSystemProvider, Path zipPath) {
        mFileSystemProvider = fileSystemProvider;
        mZipPath = zipPath;
    }

    public ZipBuilder(Path zipPath) {
        this(new ZipFileSystemProvider(), zipPath);
    }

    public ZipBuilder addContent(String path, InputStream content) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(mZipPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            ZipEntry entry = new ZipEntry(path);
            zipOutputStream.putNextEntry(entry);
            try {
                Streams.copy(content, zipOutputStream);
            } finally {
                zipOutputStream.closeEntry();
            }
        }

        return this;
    }

    public ZipBuilder addContent(String path, byte[] content) throws IOException {
        return addContent(path, new ByteArrayInputStream(content));
    }

    public FileSystem build() throws IOException {
        return mFileSystemProvider.newFileSystem(mZipPath, new HashMap<>());
    }
}
