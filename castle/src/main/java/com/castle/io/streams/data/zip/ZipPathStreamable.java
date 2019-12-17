package com.castle.io.streams.data.zip;

import com.castle.annotations.ThreadSafe;
import com.castle.io.streams.TempPathInputStream;
import com.castle.io.streams.data.ReadOnlyStreamable;
import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.regex.Pattern;

@ThreadSafe
public class ZipPathStreamable implements ReadOnlyStreamable {

    private final Zip mZip;
    private final Pattern mPattern;

    public ZipPathStreamable(Zip zip, Pattern pattern) {
        mZip = zip;
        mPattern = pattern;
    }

    @Override
    public InputStream openRead() throws IOException {
        try(OpenZip openZip = mZip.open()) {
            Path path = openZip.findFile(mPattern);
            TempPath tempPath = openZip.extract(path);

            return new TempPathInputStream(tempPath);
        }
    }
}
