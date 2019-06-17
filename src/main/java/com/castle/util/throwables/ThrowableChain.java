package com.castle.util.throwables;

import java.util.Optional;

public class ThrowableChain {

    private Throwable mFirstThrowable;

    public ThrowableChain() {
        mFirstThrowable = null;
    }

    public void chain(Throwable throwable) {
        if (mFirstThrowable == null) {
            mFirstThrowable = throwable;
        } else {
            mFirstThrowable.addSuppressed(throwable);
        }
    }

    public Optional<Throwable> getTopThrowable() {
        return Optional.ofNullable(mFirstThrowable);
    }

    public void throwIfExists() throws Throwable {
        if (mFirstThrowable != null) {
            throw mFirstThrowable;
        }
    }

    public <E extends Throwable> void throwIfType(Class<E> type) throws E {
        if (type.isInstance(mFirstThrowable)) {
            throw type.cast(mFirstThrowable);
        }
    }

    public void throwAsRuntime() {
        if (mFirstThrowable != null) {
            throw new RuntimeException(mFirstThrowable);
        }
    }
}
