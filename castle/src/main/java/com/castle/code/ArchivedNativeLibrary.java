package com.castle.code;

import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.os.Architecture;

import java.io.IOException;
import java.nio.file.Path;

public class ArchivedNativeLibrary extends TempNativeLibrary {

    private final Zip mZip;
    private final String mInZipPath;

    public ArchivedNativeLibrary(String name, Architecture architecture, Zip zip, Path inZipPath) {
        super(name, architecture);
        mZip = zip;
        mInZipPath = inZipPath.toAbsolutePath().toString();
    }

    @Override
    public TempPath getTempPath() throws IOException {
        try (OpenZip zip = mZip.open()) {
            return zip.extract(zip.getPath(mInZipPath));
        }
    }
}
