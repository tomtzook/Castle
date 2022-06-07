package com.castle.code;

import com.castle.util.os.Platform;

import java.io.IOException;
import java.nio.file.Path;

public interface LibraryClassifier {

    Platform targetPlatform(Path path) throws IOException;
}
