package com.castle.nio.zip;

import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.testutil.io.TemporaryPaths;
import com.castle.testutil.io.ZipBuilder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.castle.testutil.io.TemporaryPaths.doesAPathEndWithString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class ZipEntryExtractorTest {

    @TempDir
    Path mTemporaryFolder;
    private TempPathGenerator mTempGenerator;

    @BeforeEach
    public void setup() throws Exception {
        mTempGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder);
    }

    @Test
    public void extractToPath_dataExists_extractCorrect() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        try(FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .addContent(PATH, DATA)
                .build()) {
            Path path = zipFs.getPath(PATH);
            Path extractPath = mTempGenerator.generateFile().originalPath();

            ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mTempGenerator);
            zipEntryExtractor.extractInto(path, extractPath);

            assertArrayEquals(DATA, Files.readAllBytes(extractPath));
        }
    }

    @Test
    public void extractToPath_dataDoesNotExist_throwsIOException() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        Assertions.assertThrows(IOException.class, ()-> {
            try(FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                    .addContent(PATH, DATA)
                    .build()) {
                Path path = zipFs.getPath(PATH);
                Path extractPath = mTempGenerator.generateFile();

                Files.delete(path);

                ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mTempGenerator);
                zipEntryExtractor.extractInto(path, extractPath);
            }
        });
    }

    @Test
    public void extractToTemp_dataExists_extractCorrect() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        try(FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .addContent(PATH, DATA)
                .build()) {
            Path path = zipFs.getPath(PATH);

            ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mTempGenerator);
            try (TempPath extractPath = zipEntryExtractor.extract(path)) {
                assertArrayEquals(DATA, Files.readAllBytes(extractPath.originalPath()));
            }
        }
    }

    @Test
    public void extractToTemp_dataDoesNotExist_throwsIOException() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        Assertions.assertThrows(IOException.class, ()-> {
            try(FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                    .addContent(PATH, DATA)
                    .build()) {
                Path path = zipFs.getPath(PATH);

                Files.delete(path);

                ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mTempGenerator);
                zipEntryExtractor.extract(path);
            }
        });
    }

    @Test
    public void extractToTemp_dataDoesNotExist_tempFileDoesNotExist() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();
        final String ENDS_WITH = UUID.randomUUID().toString();

        try(FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .addContent(PATH, DATA)
                .build()) {
            Path path = zipFs.getPath(PATH);

            Files.delete(path);

            ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mTempGenerator);
            try {
                zipEntryExtractor.extract(path);
                fail("exception expected");
            } catch (IOException e) {
                assertThat(ENDS_WITH, not(doesAPathEndWithString(mTemporaryFolder)));
            }
        }
    }
}