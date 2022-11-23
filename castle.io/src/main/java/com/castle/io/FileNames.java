package com.castle.io;

import java.nio.file.Path;

public class FileNames {

    public static String extension(Path path) {
        return extension(path.getFileName().toString());
    }

    public static String extension(String path) {
        int extIndex = path.lastIndexOf('.');
        if (extIndex < 0) {
            return "";
        }

        return path.substring(extIndex + 1);
    }
}
