package com.castle.code;

import com.castle.util.io.FilenameHelper;
import com.castle.util.os.Platform;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

public class FileNativeLibrary implements NativeLibrary {

    private final Path mPath;
    private final Platform mTargetPlatform;

    public FileNativeLibrary(Path path, Platform targetPlatform) {
        mPath = path;
        mTargetPlatform = targetPlatform;
    }

    public Path getPath() {
        return mPath;
    }

    @Override
    public String getName() {
        return FilenameHelper.removeFileExtension(mPath.getName(mPath.getNameCount() - 1).toString());
    }

    @Override
    public Platform getTargetArchitecture() {
        return mTargetPlatform;
    }

    @Override
    public InputStream openRead() throws IOException {
        return new FileInputStream(mPath.toFile());
    }
}
