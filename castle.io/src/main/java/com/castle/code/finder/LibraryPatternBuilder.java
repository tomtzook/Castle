package com.castle.code.finder;

import com.castle.util.os.Platform;

import java.util.regex.Pattern;

public interface LibraryPatternBuilder {

    Pattern build(String name);
    Pattern build(Platform targetPlatform, String name);
}
