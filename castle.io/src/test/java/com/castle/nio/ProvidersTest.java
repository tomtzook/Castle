package com.castle.nio;

import com.castle.nio.temp.TempPathGenerator;
import com.castle.testutil.io.TemporaryPaths;
import com.castle.testutil.io.ZipHelper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ProvidersTest {

    @TempDir
    public Path mTemporaryFolder;
    private TempPathGenerator mTempPathGenerator;

    @BeforeEach
    public void setup() throws Exception {
        mTempPathGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder);
    }

    @Test
    public void zipProvider_withFiles_canAccessZipFiles() throws Exception {
        final String ENTRY_PATH = "data";
        final byte[] CONTENT = "asdaf".getBytes();

        Path zipPath = mTempPathGenerator.generateFile().originalPath();
        ZipHelper.putEntry(zipPath, ENTRY_PATH, CONTENT);

        FileSystemProvider provider = Providers.zipProvider();
        try (FileSystem fileSystem = provider.newFileSystem(zipPath, new HashMap<>())) {
            Path path = fileSystem.getPath(ENTRY_PATH);
            assertArrayEquals(CONTENT, Files.readAllBytes(path));
        }
    }
}