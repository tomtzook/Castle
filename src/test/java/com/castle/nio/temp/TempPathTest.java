package com.castle.nio.temp;

import com.castle.testutil.io.FakeFileSystemProvider;
import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.spi.FileSystemProvider;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TempPathTest {

    @Test
    public void close_normal_deletesFileIfExists() throws Exception {
        FileSystemProvider provider = spy(new FakeFileSystemProvider());
        Path path = mock(Path.class);

        TempPath tempPath = new TempPath(provider, path);
        tempPath.close();

        verify(provider, times(1)).deleteIfExists(eq(path));
    }
}