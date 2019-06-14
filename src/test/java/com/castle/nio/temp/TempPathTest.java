package com.castle.nio.temp;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class TempPathTest {

    @Test
    public void close_normal_deletesFileIfExists() throws Exception {
        FileSystemProvider provider = mock(FileSystemProvider.class);
        Path path = mock(Path.class);

        TempPath tempPath = new TempPath(provider, path);
        tempPath.close();

        verify(provider, times(1)).deleteIfExists(eq(path))
    }
}