package com.castle.testutil;

import org.hamcrest.BaseMatcher;

public abstract class TypedBaseMatcher<T> extends BaseMatcher<T> {

    private final Class<T> mType;

    protected TypedBaseMatcher(Class<T> type) {
        mType = type;
    }

    @Override
    public boolean matches(Object o) {
        if (!mType.isInstance(o)) {
            return false;
        }

        return doesMatch(mType.cast(o));
    }

    public abstract boolean doesMatch(T value);
}
