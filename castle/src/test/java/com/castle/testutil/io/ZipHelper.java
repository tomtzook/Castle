package com.castle.testutil.io;

import com.castle.io.streams.Streams;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipHelper {

    private ZipHelper() {}

    public static void putEntry(Path zipPath, String inZipPath, InputStream content) throws IOException {
        try (OutputStream outputStream = Files.newOutputStream(zipPath);
             ZipOutputStream zipOutputStream = new ZipOutputStream(outputStream)) {
            ZipEntry entry = new ZipEntry(inZipPath);
            zipOutputStream.putNextEntry(entry);
            try {
                Streams.copy(content, zipOutputStream);
            } finally {
                zipOutputStream.closeEntry();
            }
        }
    }

    public static void putEntry(Path zipPath, String inZipPath, byte[] content) throws IOException {
        putEntry(zipPath, inZipPath, new ByteArrayInputStream(content));
    }
}
