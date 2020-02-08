package com.castle.util.os;

import com.castle.annotations.Immutable;

import java.util.Objects;

@Immutable
public class Architecture {

    private final OperatingSystem mOperatingSystem;
    private final String mArchName;

    public Architecture(OperatingSystem operatingSystem, String archName) {
        mOperatingSystem = operatingSystem;
        mArchName = archName;
    }

    public OperatingSystem getOperatingSystem() {
        return mOperatingSystem;
    }

    public String getArchName() {
        return mArchName;
    }

    public boolean equals(Architecture other) {
        return mOperatingSystem == other.mOperatingSystem &&
                mArchName.equals(other.mArchName);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Architecture && equals((Architecture)obj);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mOperatingSystem, mArchName);
    }
}
