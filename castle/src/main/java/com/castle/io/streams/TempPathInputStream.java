package com.castle.io.streams;

import com.castle.nio.temp.TempPath;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class TempPathInputStream extends FileInputStream {

    private final TempPath mPath;

    public TempPathInputStream(TempPath path) throws FileNotFoundException {
        super(path.originalPath().toFile());
        mPath = path;
    }

    @Override
    public void close() throws IOException {
        super.close();
        mPath.close();
    }
}
