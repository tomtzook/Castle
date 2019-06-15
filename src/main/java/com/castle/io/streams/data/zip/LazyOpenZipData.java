package com.castle.io.streams.data.zip;

import com.castle.io.streams.TempPathInputStream;
import com.castle.io.streams.data.StreamableData;
import com.castle.nio.temp.TempPath;
import com.castle.zip.OpenZip;
import com.castle.zip.Zip;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.regex.Pattern;

public class LazyOpenZipData implements StreamableData {

    private final Zip mZip;
    private final Pattern mPattern;

    public LazyOpenZipData(Zip zip, Pattern pattern) {
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
