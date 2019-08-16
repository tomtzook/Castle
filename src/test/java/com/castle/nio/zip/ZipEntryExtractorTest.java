package com.castle.nio.zip;

import com.castle.nio.temp.TempPathGenerator;
import com.castle.testutil.TemporaryPaths;
import com.castle.testutil.ZipBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.Assert.*;

public class ZipEntryExtractorTest {

    @Rule
    public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Test
    public void extractToPath_dataExists_extractCorrect() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        FileSystem zipFs = new ZipBuilder(mTemporaryFolder.newFile("test.zip").toPath())
                .addContent(PATH, DATA)
                .build();

        Path path = zipFs.getPath(PATH);
        Path extractPath = mTemporaryFolder.newFile().toPath();

        TempPathGenerator mPathGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder);

        ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mPathGenerator);
        zipEntryExtractor.extractInto(path, extractPath);

        assertArrayEquals(DATA, Files.readAllBytes(extractPath));
    }
}