package com.castle.util.throwables;

import com.castle.annotations.NotThreadSafe;

import java.util.Optional;
import java.util.function.Function;

@NotThreadSafe
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

    public void throwAsRuntime() {
        Throwables.throwAsRuntime(mFirstThrowable);
    }

    public <E extends Throwable> void throwAsType(Class<E> type, Function<Throwable, E> converter) throws E {
        Throwables.throwAsType(mFirstThrowable, type, converter);
    }
}
