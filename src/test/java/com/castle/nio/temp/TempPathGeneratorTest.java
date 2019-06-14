package com.castle.nio.temp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.spi.FileSystemProvider;
import java.util.Random;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class TempPathGeneratorTest {

    @Rule
    public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    private Path mParent;
    private FileSystemProvider mFileSystemProvider;

    @Before
    public void setup() throws Exception {
        mParent = mTemporaryFolder.getRoot().toPath();
        mFileSystemProvider = mParent.getFileSystem().provider();
    }

    @Test
    public void generatePath_withPrefixAndSuffix_pathContainsPrefixAndSuffix() throws Exception {
        final String PREFIX = "prefix";
        final String SUFFIX = "suffix";

        TempPathGenerator tempPathGenerator = createGenerator(PREFIX, SUFFIX);
        Path path = tempPathGenerator.generatePath();

        assertThat(path.getFileName().toString(), startsWith(PREFIX));
        assertThat(path.getFileName().toString(), endsWith(SUFFIX));
    }

    @Test
    public void generatePath_withParent_pathIsChildOfParent() throws Exception {
        TempPathGenerator tempPathGenerator = createGenerator();
        Path path = tempPathGenerator.generatePath();

        assertThat(path.getParent(), equalTo(mParent));
    }

    @Test
    public void generatePath_severalTimes_eachPathIsDifferent() throws Exception {
        final int SIZE = 20;

        TempPathGenerator tempPathGenerator = createGenerator();

        long size = IntStream.range(0, SIZE)
                .mapToObj(i -> tempPathGenerator.generatePath())
                .distinct()
                .count();

        assertEquals(SIZE, size);
    }

    @Test
    public void generateFile_normal_createsFile() throws Exception {
        TempPathGenerator tempPathGenerator = createGenerator();
        TempPath tempPath = tempPathGenerator.generateFile();

        assertTrue(isFile(tempPath.originalPath()));
    }

    @Test
    public void generateDirectory_normal_createsDirectory() throws Exception {
        TempPathGenerator tempPathGenerator = createGenerator();
        TempPath tempPath = tempPathGenerator.generateDirectory();

        assertTrue(isDirectory(tempPath.originalPath()));
    }

    private TempPathGenerator createGenerator() {
        return createGenerator("", "");
    }

    private TempPathGenerator createGenerator(String prefix, String suffix) {
        return new TempPathGenerator(mFileSystemProvider,
                mParent,
                new Random(),
                prefix, suffix);
    }

    private boolean isFile(Path path) throws IOException {
        return mFileSystemProvider.readAttributes(path, BasicFileAttributes.class).isRegularFile();
    }

    private boolean isDirectory(Path path) throws IOException {
        return mFileSystemProvider.readAttributes(path, BasicFileAttributes.class).isDirectory();
    }
}