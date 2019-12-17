package com.castle.util.os;

import com.castle.annotations.Immutable;

@Immutable
public class Architecture {

    private final String mName;

    public Architecture(String name) {
        mName = name;
    }

    public String getName() {
        return mName;
    }

    public boolean equals(Architecture other) {
        return mName.equals(other.mName);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Architecture && equals((Architecture)obj);
    }

    @Override
    public int hashCode() {
        return mName.hashCode();
    }
}
