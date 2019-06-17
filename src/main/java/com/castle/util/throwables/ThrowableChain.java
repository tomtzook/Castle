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
        Throwables.throwIfType(mFirstThrowable, type);
    }

    public void throwAsRuntime() throws RuntimeException {
        Throwables.throwAsRuntime(mFirstThrowable);
    }

    public Optional<RuntimeException> getAsRuntime() {
        if (mFirstThrowable == null) {
            return Optional.empty();
        }

        return Optional.of(new RuntimeException(mFirstThrowable));
    }
}
