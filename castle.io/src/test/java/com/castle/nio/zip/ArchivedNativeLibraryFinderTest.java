package com.castle.nio.zip;

import com.castle.code.NativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.testutil.io.TemporaryPaths;
import com.castle.testutil.io.ZipBuilder;
import com.castle.testutil.io.ZipMock;
import com.castle.io.streams.IoStreams;
import com.castle.util.os.Architecture;
import com.castle.util.os.OperatingSystem;
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
        final Architecture ARCH = new Architecture(OperatingSystem.Linux, "test");

        try (FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .addContent(String.format("%s/%s/%s",
                        ARCH.getOperatingSystem().name().toLowerCase(),
                        ARCH.getArchName(),
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
        final Architecture ARCH = new Architecture(OperatingSystem.Linux, "test");
        final Architecture ACTUAL_ARCH = new Architecture(OperatingSystem.Linux, "boya");

        try (FileSystem zipFs = new ZipBuilder(mTemporaryFolder.resolve("test.zip"))
                .addContent(String.format("%s/%s/%s",
                        ARCH.getOperatingSystem().name().toLowerCase(),
                        ARCH.getArchName(),
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
        final Architecture ARCH = new Architecture(OperatingSystem.Linux, "test");

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