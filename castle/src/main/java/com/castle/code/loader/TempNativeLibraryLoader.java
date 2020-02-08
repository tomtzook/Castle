package com.castle.code.loader;

import com.castle.annotations.Immutable;
import com.castle.code.NativeLibrary;
import com.castle.code.TempNativeLibrary;
import com.castle.exceptions.CodeLoadException;
import com.castle.io.streams.Streams;
import com.castle.nio.temp.TempPath;
import com.castle.nio.temp.TempPathGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

@Immutable
public class TempNativeLibraryLoader implements NativeLibraryLoader {

    private final TempPathGenerator mPathGenerator;

    public TempNativeLibraryLoader(TempPathGenerator pathGenerator) {
        mPathGenerator = pathGenerator;
    }

    @Override
    public void load(NativeLibrary nativeLibrary) throws CodeLoadException {
        if (nativeLibrary instanceof TempNativeLibrary) {
            loadFromTemp((TempNativeLibrary) nativeLibrary);
        } else {
            generateTempAndLoad(nativeLibrary);
        }
    }

    private void loadFromTemp(TempNativeLibrary nativeLibrary) throws CodeLoadException {
        try (TempPath tempPath = nativeLibrary.makeTempFile()) {
            System.load(tempPath.toString());
        } catch (IOException e) {
            throw new CodeLoadException(e);
        }
    }

    private void generateTempAndLoad(NativeLibrary nativeLibrary) throws CodeLoadException {
        try (TempPath tempPath = mPathGenerator.generateFile()) {
            try (OutputStream tempOutputStream = Files.newOutputStream(tempPath);
                 InputStream codeStream = nativeLibrary.openRead()) {
                Streams.copy(codeStream, tempOutputStream);
            }

            System.load(tempPath.toAbsolutePath().toString());
        } catch (IOException e) {
            throw new CodeLoadException(e);
        }
    }
}
