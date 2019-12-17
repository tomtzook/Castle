package com.castle.io.streams.data.zip;

import com.castle.annotations.Immutable;
import com.castle.io.streams.TempPathInputStream;
import com.castle.io.streams.data.ReadOnlyStreamable;
import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.ZipEntryExtractor;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

@Immutable
public class OpenZipPathStreamable implements ReadOnlyStreamable {

    private final ZipEntryExtractor mEntryExtractor;
    private final Path mPath;

    public OpenZipPathStreamable(ZipEntryExtractor entryExtractor, Path path) {
        mEntryExtractor = entryExtractor;
        mPath = path;
    }

    @Override
    public InputStream openRead() throws IOException {
        TempPath tempPath = mEntryExtractor.extract(mPath);
        return new TempPathInputStream(tempPath);
    }
}
