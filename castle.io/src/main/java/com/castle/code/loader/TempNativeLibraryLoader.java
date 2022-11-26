package com.castle.code.loader;

import com.castle.annotations.NotThreadSafe;
import com.castle.code.NativeLibrary;
import com.castle.exceptions.CodeLoadException;
import com.castle.io.streams.IoStreams;
import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.util.closeables.Closer;
import com.castle.util.os.KnownOperatingSystem;
import com.castle.util.os.Platform;
import com.castle.util.os.System;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@NotThreadSafe
public class TempNativeLibraryLoader implements NativeLibraryLoader {

    private final TempPathGenerator mPathGenerator;
    private final Platform mCurrentPlatform;

    public TempNativeLibraryLoader(TempPathGenerator pathGenerator, Platform currentPlatform) {
        mPathGenerator = pathGenerator;
        mCurrentPlatform = currentPlatform;
    }

    public TempNativeLibraryLoader() {
        this(new TempPathGenerator(
                "lib",
                        System.platform().getOperatingSystem().nativeLibraryExtension()),
                System.platform());
    }

    @Override
    public boolean supports(NativeLibrary nativeLibrary) {
        return nativeLibrary.getTargetArchitecture().equals(mCurrentPlatform);
    }

    @Override
    public void load(NativeLibrary nativeLibrary) throws CodeLoadException {
        if (!supports(nativeLibrary)) {
            throw new IllegalArgumentException("unsupported library");
        }

        generateTempAndLoad(nativeLibrary);
    }

    private void generateTempAndLoad(NativeLibrary nativeLibrary) throws CodeLoadException {
        try (Closer closer = Closer.empty()) {
            TempPath tempPath = mPathGenerator.generateFile();
            if (!mCurrentPlatform.getOperatingSystem().equals(KnownOperatingSystem.WINDOWS)) {
                closer.add(tempPath);
            }

            try (OutputStream tempOutputStream = Files.newOutputStream(tempPath);
                 InputStream codeStream = nativeLibrary.openRead()) {
                IoStreams.copy(codeStream, tempOutputStream);
            }

            java.lang.System.load(tempPath.toAbsolutePath().toString());
        } catch (Exception e) {
            throw new CodeLoadException(e);
        }
    }
}
