package com.castle.code.finder;

import com.castle.util.os.Platform;

import java.util.regex.Pattern;

public class DefaultLibraryPatternBuilder implements LibraryPatternBuilder {

    @Override
    public Pattern build(String name) {
        return Pattern.compile(String.format(".*%s\\.(dll|so|dylib)$",
                name));
    }

    @Override
    public Pattern build(Platform targetPlatform, String name) {
        return Pattern.compile(String.format(".*%s\\/%s\\/.*%s\\.(dll|so|dylib)$",
                targetPlatform.getOperatingSystem().name().toLowerCase(),
                targetPlatform.getArchitecture().name().toLowerCase(),
                name));
    }
}
