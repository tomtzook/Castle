package com.castle.nio.temp;

import org.junit.Before;
import org.junit.Test;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;
import java.util.Random;
import java.util.stream.IntStream;

import static org.hamcrest.CoreMatchers.endsWith;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.junit.Assert.*;

public class TempPathGeneratorTest {

    private FileSystemProvider mFileSystemProvider;
    private Path mParent;

    @Before
    public void setup() throws Exception {
        FileSystem fileSystem = FileSystems.getDefault();

        mFileSystemProvider = fileSystem.provider();
        mParent = fileSystem.getRootDirectories().iterator().next();
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

    private TempPathGenerator createGenerator() {
        return createGenerator("", "");
    }

    private TempPathGenerator createGenerator(String prefix, String suffix) {
        return new TempPathGenerator(mFileSystemProvider,
                mParent,
                new Random(),
                prefix, suffix);
    }
}