package com.castle.util.os;

import com.castle.annotations.Stateless;

@Stateless
public class System {

    private static final String X86_ARCH = "x86";

    private System() {}

    public static Bitness bitness() {
        String property = java.lang.System.getProperty("os.arch");
        if (X86_ARCH.equalsIgnoreCase(property)) {
            return Bitness.x86;
        }

        return Bitness.x64;
    }

    public static OperatingSystem operatingSystem() {
        String osName = java.lang.System.getProperty("os.name").toLowerCase();
        for (OperatingSystem operatingSystem : OperatingSystem.values()) {
            if (operatingSystem.doesNameMatch(osName)) {
                return operatingSystem;
            }
        }

        throw new Error("unable to find current platform: " + osName);
    }

    public static Architecture architecture() {
        return new Architecture(operatingSystem(), java.lang.System.getProperty("os.arch"));
    }
}
