package com.castle.util.io;

public class FilenameHelper {

    public static String removeFileExtension(String filename, boolean removeAllExtensions) {
        if (filename == null || filename.isEmpty()) {
            return filename;
        }

        String extPattern = "(?<!^)[.]" + (removeAllExtensions ? ".*" : "[^.]*$");
        return filename.replaceAll(extPattern, "");
    }

    public static String removeFileExtension(String filename) {
        return removeFileExtension(filename, false);
    }
}
