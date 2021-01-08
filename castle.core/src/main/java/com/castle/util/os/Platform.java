package com.castle.util.os;

import com.castle.annotations.Immutable;

import java.util.Objects;

@Immutable
public class Platform {

    private final OperatingSystem mOperatingSystem;
    private final Architecture mArchitecture;

    public Platform(OperatingSystem operatingSystem, Architecture architecture) {
        mOperatingSystem = operatingSystem;
        mArchitecture = architecture;
    }

    public OperatingSystem getOperatingSystem() {
        return mOperatingSystem;
    }

    public Architecture getArchitecture() {
        return mArchitecture;
    }

    public boolean equals(Platform other) {
        return mOperatingSystem == other.mOperatingSystem &&
                mArchitecture.equals(other.mArchitecture);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Platform && equals((Platform) obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mOperatingSystem, mArchitecture);
    }
}
