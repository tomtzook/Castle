package com.castle.util.os;

public class OperatingSystem {

    private static final String X86_ARCH = "x86";

    public Bitness getBitness() {
        String property = System.getProperty("os.arch");
        if (X86_ARCH.equalsIgnoreCase(property)) {
            return Bitness.x86;
        }

        return Bitness.x64;
    }

    public Platform getCurrentPlatform() {
        String osName = System.getProperty("os.name").toLowerCase();
        for (Platform platform : Platform.values()) {
            if (platform.doesNameMatch(osName)) {
                return platform;
            }
        }

        throw new Error("unable to find current platform");
    }

    public Architecture getCurrentArchitecture() {
        return new Architecture(getCurrentPlatform(), getBitness());
    }
}
