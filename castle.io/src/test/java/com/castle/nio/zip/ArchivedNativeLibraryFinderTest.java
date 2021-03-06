package com.castle.nio.zip;

import com.castle.code.NativeLibrary;
import com.castle.code.finder.ArchivedNativeLibraryFinder;
import com.castle.exceptions.FindException;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.testutil.io.TemporaryPaths;
import com.castle.testutil.io.ZipBuilder;
import com.castle.testutil.io.ZipMock;
import com.castle.io.streams.IoStreams;
import com.castle.util.os.KnownArchitecture;
import com.castle.util.os.Platform;
import com.castle.util.os.KnownOperatingSystem;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.InputStream;
import java.nio.file.FileSystem;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class ArchivedNativeLibraryFinderTest {

    @TempDir
    Path mTemporaryFolder;
    private TempPathGenerator mTempGenerator;

    @BeforeEach
    public void setup() throws Exception {
        mTempGenerator = TemporaryPaths.pathGenerator(mTemporaryFolder);
    }

    @Test
    public void find_libraryExistsInArchive_findsCorrectLibrary() throws Exception {
        final byte[] DATA = "somedata".getBytes();
        final String LIBNAME = "libtest";
        final String FULL_LIB_NAME = LIBNAME.concat(".so");
        final Platform ARCH = new Platform(KnownOperatingSystem.LINUX, KnownArchitecture.AMD64);

        try (FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .addContent(String.format("%s/%s/%s",
                        ARCH.getOperatingSystem().name().toLowerCase(),
                        ARCH.getArchitecture().name().toLowerCase(),
                        FULL_LIB_NAME), DATA)
                .build()) {
            Zip zip = ZipMock.fromFileSystem(zipFs);

            ArchivedNativeLibraryFinder finder = new ArchivedNativeLibraryFinder(zip, ARCH);
            NativeLibrary library = finder.find(LIBNAME);

            try (InputStream codeStream = library.openRead()) {
                assertArrayEquals(DATA, IoStreams.readAll(codeStream));
            }
        }
    }

    @Test
    public void find_libraryOfWrongArchitectureExists_throwsFindException() throws Exception {
        final byte[] DATA = "somedata".getBytes();
        final String LIBNAME = "libtest";
        final String FULL_LIB_NAME = LIBNAME.concat(".so");
        final Platform ARCH = new Platform(KnownOperatingSystem.LINUX, KnownArchitecture.AARCH64);
        final Platform ACTUAL_ARCH = new Platform(KnownOperatingSystem.LINUX, KnownArchitecture.AMD64);

        try (FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .addContent(String.format("%s/%s/%s",
                        ARCH.getOperatingSystem().name().toLowerCase(),
                        ARCH.getArchitecture(),
                        FULL_LIB_NAME), DATA)
                .build()) {
            Zip zip = ZipMock.fromFileSystem(zipFs);

            ArchivedNativeLibraryFinder finder = new ArchivedNativeLibraryFinder(zip, ACTUAL_ARCH);

            Assertions.assertThrows(FindException.class, () -> {
                finder.find(LIBNAME);
            });
        }
    }

    @Test
    public void find_libraryNotExists_throwsFindException() throws Exception {
        final String LIBNAME = "libtest";
        final Platform ARCH = new Platform(KnownOperatingSystem.LINUX, KnownArchitecture.AMD64);

        try (FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .build()) {
            Zip zip = ZipMock.fromFileSystem(zipFs);

            ArchivedNativeLibraryFinder finder = new ArchivedNativeLibraryFinder(zip, ARCH);

            Assertions.assertThrows(FindException.class, () -> {
                finder.find(LIBNAME);
            });
        }
    }
}