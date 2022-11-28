package com.castle.code;

import com.castle.annotations.NotThreadSafe;
import com.castle.io.streams.TempPathInputStream;
import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.os.Platform;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@NotThreadSafe
public class ArchivedNativeLibrary extends NativeLibraryBase {

    private final Zip mZip;
    private final String mInZipPath;

    public ArchivedNativeLibrary(Platform platform, Zip zip, String inZipPath) {
        super(new File(inZipPath).getName(), platform);
        mZip = zip;
        mInZipPath = inZipPath;
    }

    @Override
    public InputStream openRead() throws IOException {
        try (OpenZip zip = mZip.open()) {
            TempPath tempPath = zip.extract(zip.getPath(mInZipPath));
            return new TempPathInputStream(tempPath);
        }
    }
}
