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
        for (KnownOperatingSystem operatingSystem : KnownOperatingSystem.values()) {
            if (operatingSystem.isCurrent()) {
                return operatingSystem;
            }
        }

        return KnownOperatingSystem.UNKNOWN;
    }

    public static Platform platform() {
        return new Platform(operatingSystem(), architecture());
    }
}
