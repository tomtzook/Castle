package com.castle.nio.zip;

import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.testutil.TemporaryPaths;
import com.castle.testutil.ZipBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import static com.castle.testutil.TemporaryPaths.doesAPathEndWithString;
import static org.hamcrest.Matchers.not;
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

    @Test(expected = IOException.class)
    public void extractToPath_dataDoesNotExist_throwsIOException() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        FileSystem zipFs = new ZipBuilder(mTemporaryFolder.newFile("test.zip").toPath())
                .addContent(PATH, DATA)
                .build();

        Path path = zipFs.getPath(PATH);
        Path extractPath = mTemporaryFolder.newFile().toPath();

        Files.delete(path);

        TempPathGenerator mPathGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder);

        ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mPathGenerator);
        zipEntryExtractor.extractInto(path, extractPath);
    }

    @Test
    public void extractToTemp_dataExists_extractCorrect() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        FileSystem zipFs = new ZipBuilder(mTemporaryFolder.newFile("test.zip").toPath())
                .addContent(PATH, DATA)
                .build();

        Path path = zipFs.getPath(PATH);

        TempPathGenerator mPathGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder);

        ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mPathGenerator);
        try (TempPath extractPath = zipEntryExtractor.extract(path)) {
            assertArrayEquals(DATA, Files.readAllBytes(extractPath.originalPath()));
        }
    }

    @Test(expected = IOException.class)
    public void extractToTemp_dataDoesNotExist_throwsIOException() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();

        FileSystem zipFs = new ZipBuilder(mTemporaryFolder.newFile("test.zip").toPath())
                .addContent(PATH, DATA)
                .build();

        Path path = zipFs.getPath(PATH);

        Files.delete(path);

        TempPathGenerator mPathGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder);

        ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mPathGenerator);
        zipEntryExtractor.extract(path);
    }

    @Test
    public void extractToTemp_dataDoesNotExist_tempFileDoesNotExist() throws Exception {
        final String PATH = "data";
        final byte[] DATA = "somedata".getBytes();
        final String ENDS_WITH = UUID.randomUUID().toString();

        FileSystem zipFs = new ZipBuilder(mTemporaryFolder.newFile("test.zip").toPath())
                .addContent(PATH, DATA)
                .build();

        Path path = zipFs.getPath(PATH);

        Files.delete(path);

        TempPathGenerator mPathGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder, "", ENDS_WITH);

        ZipEntryExtractor zipEntryExtractor = new ZipEntryExtractor(mPathGenerator);
        try {
            zipEntryExtractor.extract(path);
            fail("exception expected");
        } catch (IOException e) {
            assertThat(ENDS_WITH, not(doesAPathEndWithString(mTemporaryFolder.getRoot().toPath())));
        }
    }
}