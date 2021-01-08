package com.castle.code;

import com.castle.annotations.NotThreadSafe;
import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.os.Platform;

import java.io.IOException;
import java.nio.file.Path;

@NotThreadSafe
public class ArchivedNativeLibrary extends TempNativeLibrary {

    private final Zip mZip;
    private final String mInZipPath;

    public ArchivedNativeLibrary(String name, Platform platform, Zip zip, String inZipPath) {
        super(name, platform);
        mZip = zip;
        mInZipPath = inZipPath;
    }

    public ArchivedNativeLibrary(String name, Platform platform, Zip zip, Path inZipPath) {
        this(name, platform, zip, inZipPath.toAbsolutePath().toString());
    }

    @Override
    public TempPath makeTempFile() throws IOException {
        try (OpenZip zip = mZip.open()) {
            return zip.extract(zip.getPath(mInZipPath));
        }
    }
}
