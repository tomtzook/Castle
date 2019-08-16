package com.castle.util.os;

public enum Bitness {
    x86("86"),
    x64("64");

    private final String mDisplayName;

    Bitness(String displayName) {
        mDisplayName = displayName;
    }

    public String displayName() {
        return mDisplayName;
    }
}
