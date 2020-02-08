package com.castle.code.loader;

import com.castle.annotations.Immutable;
import com.castle.code.NativeLibrary;
import com.castle.code.TempNativeLibrary;
import com.castle.exceptions.CodeLoadException;
import com.castle.io.streams.IoStreams;
import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;
import com.castle.util.os.Architecture;
import com.castle.util.os.System;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@Immutable
public class TempNativeLibraryLoader implements NativeLibraryLoader {

    private final TempPathGenerator mPathGenerator;
    private final Architecture mCurrentArchitecture;

    public TempNativeLibraryLoader(TempPathGenerator pathGenerator, Architecture currentArchitecture) {
        mPathGenerator = pathGenerator;
        mCurrentArchitecture = currentArchitecture;
    }

    public TempNativeLibraryLoader() {
        this(new TempPathGenerator(), System.architecture());
    }

    @Override
    public void load(NativeLibrary nativeLibrary) throws CodeLoadException {
        if (!mCurrentArchitecture.equals(nativeLibrary.getTargetArchitecture())) {
            throw new IllegalArgumentException(String.format("library (%s) doesn't support current platform (%s)",
                    nativeLibrary.getTargetArchitecture(),
                    mCurrentArchitecture));
        }

        if (nativeLibrary instanceof TempNativeLibrary) {
            loadFromTemp((TempNativeLibrary) nativeLibrary);
        } else {
            generateTempAndLoad(nativeLibrary);
        }
    }

    private void loadFromTemp(TempNativeLibrary nativeLibrary) throws CodeLoadException {
        try (TempPath tempPath = nativeLibrary.makeTempFile()) {
            java.lang.System.load(tempPath.toString());
        } catch (IOException e) {
            throw new CodeLoadException(e);
        }
    }

    private void generateTempAndLoad(NativeLibrary nativeLibrary) throws CodeLoadException {
        try (TempPath tempPath = mPathGenerator.generateFile()) {
            try (OutputStream tempOutputStream = Files.newOutputStream(tempPath);
                 InputStream codeStream = nativeLibrary.openRead()) {
                IoStreams.copy(codeStream, tempOutputStream);
            }

            java.lang.System.load(tempPath.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new CodeLoadException(e);
        }
    }
}
