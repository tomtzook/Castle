package com.castle.util.os;

public class System {

    private static final String X86_ARCH = "x86";

    public Bitness getBitness() {
        String property = java.lang.System.getProperty("os.arch");
        if (X86_ARCH.equalsIgnoreCase(property)) {
            return Bitness.x86;
        }

        return Bitness.x64;
    }

    public OperatingSystem getOperatingSystem() {
        String osName = java.lang.System.getProperty("os.name").toLowerCase();
        for (OperatingSystem operatingSystem : OperatingSystem.values()) {
            if (operatingSystem.doesNameMatch(osName)) {
                return operatingSystem;
            }
        }

        throw new Error("unable to find current platform");
    }
}
