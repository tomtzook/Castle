package com.castle.nio;

import com.castle.testutil.io.ZipHelper;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;

import static org.junit.Assert.*;

public class ProvidersTest {

    @Rule
    public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void zipProvider_withFiles_canAccessZipFiles() throws Exception {
        final String ENTRY_PATH = "data";
        final byte[] CONTENT = "asdaf".getBytes();

        Path zipPath = mTemporaryFolder.newFile().toPath();
        ZipHelper.putEntry(zipPath, ENTRY_PATH, CONTENT);

        FileSystemProvider provider = Providers.zipProvider();
        try (FileSystem fileSystem = provider.newFileSystem(zipPath, new HashMap<>())) {
            Path path = fileSystem.getPath(ENTRY_PATH);
            assertArrayEquals(CONTENT, Files.readAllBytes(path));
        }
    }
}