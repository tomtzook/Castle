package com.castle.code;

import com.castle.annotations.NotThreadSafe;
import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.os.Platform;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

@NotThreadSafe
public class ArchivedNativeLibrary extends TempNativeLibrary {

    private final Zip mZip;
    private final String mInZipPath;

    public ArchivedNativeLibrary(Platform platform, Zip zip, String inZipPath) {
        super(new File(inZipPath).getName(), platform);
        mZip = zip;
        mInZipPath = inZipPath;
    }

    @Override
    public TempPath makeTempFile() throws IOException {
        try (OpenZip zip = mZip.open()) {
            return zip.extract(zip.getPath(mInZipPath));
        }
    }
}
