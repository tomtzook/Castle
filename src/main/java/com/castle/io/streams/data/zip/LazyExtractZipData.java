package com.castle.io.streams.data.zip;

import com.castle.io.streams.TempPathInputStream;
import com.castle.io.streams.data.StreamableData;
import com.castle.nio.temp.TempPath;
import com.castle.zip.ZipEntryExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class LazyExtractZipData implements StreamableData {

    private final ZipEntryExtractor mEntryExtractor;
    private final Path mPath;

    public LazyExtractZipData(ZipEntryExtractor entryExtractor, Path path) {
        mEntryExtractor = entryExtractor;
        mPath = path;
    }

    @Override
    public InputStream openRead() throws IOException {
        TempPath tempPath = mEntryExtractor.extract(mPath);
        return new TempPathInputStream(tempPath);
    }
}
