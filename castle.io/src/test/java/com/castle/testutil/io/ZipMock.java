package com.castle.testutil.io;

import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.closeables.ReferenceCounter;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.util.concurrent.locks.Lock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ZipMock {

    private ZipMock() {
    }

    public static Zip fromFileSystem(FileSystem fileSystem) throws IOException {
        Zip zip = mock(Zip.class);
        OpenZip openZip = new OpenZip(fileSystem, mock(ReferenceCounter.class), mock(Lock.class));
        when(zip.open()).thenReturn(openZip);

        return zip;
    }
}
