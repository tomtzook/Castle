package com.castle.util.os;

import com.castle.annotations.Stateless;

@Stateless
public class System {

    private System() {
    }

    public static Bitness bitness() {
        return architecture().bitness();
    }

    public static Architecture architecture() {
        for (KnownArchitecture architecture : KnownArchitecture.values()) {
            if (architecture.isCurrent()) {
                return architecture;
            }
        }

        return KnownArchitecture.UNKNOWN;
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

    public static Platform platform() {
        return new Platform(operatingSystem(), architecture());
    }
}
