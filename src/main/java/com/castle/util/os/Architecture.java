package com.castle.util.os;

public class Architecture {

    private final Platform mPlatform;
    private final Bitness mBitness;

    public Architecture(Platform platform, Bitness bitness) {
        mPlatform = platform;
        mBitness = bitness;
    }

    public Platform getPlatform() {
        return mPlatform;
    }

    public Bitness getBitness() {
        return mBitness;
    }
}
