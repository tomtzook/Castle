package com.castle.io.streams.data.zip;

import com.castle.annotations.ThreadSafe;
import com.castle.nio.temp.TempPath;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.io.streams.TempPathInputStream;
import com.castle.io.streams.data.ReadOnlyStreamable;
import com.castle.util.function.ThrowingFunction;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.regex.Pattern;

@ThreadSafe
public class ZipPathStreamable implements ReadOnlyStreamable {

    private final Zip mZip;
    private final ThrowingFunction<OpenZip, Path, IOException> mPathFinder;

    public ZipPathStreamable(Zip zip, ThrowingFunction<OpenZip, Path, IOException> pathFinder) {
        mZip = zip;
        mPathFinder = pathFinder;
    }

    public ZipPathStreamable(Zip zip, Pattern pattern) {
        this(zip, (openZip) -> openZip.findFile(pattern));
    }

    @Override
    public InputStream openRead() throws IOException {
        try (OpenZip openZip = mZip.open()) {
            Path path = mPathFinder.apply(openZip);
            TempPath tempPath = openZip.extract(path);

            return new TempPathInputStream(tempPath);
        }
    }
}
