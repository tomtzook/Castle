package com.castle.io.streams;

import com.castle.annotations.NotThreadSafe;
import com.castle.nio.temp.TempPath;
import com.castle.util.closeables.Closer;
import com.castle.util.throwables.Throwables;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@NotThreadSafe
public class TempPathInputStream extends FileInputStream {

    private final TempPath mPath;

    public TempPathInputStream(TempPath path) throws FileNotFoundException {
        super(path.originalPath().toFile());
        mPath = path;
    }

    @Override
    public void close() throws IOException {
        try {
            Closer.empty()
                    .addAll(super::close, mPath)
                    .close();
        } catch (Exception e) {
            Throwables.throwAsType(e, IOException.class, IOException::new);
        }
    }
}
