package com.castle.util.throwables;

import com.castle.annotations.NotThreadSafe;
import com.castle.exceptions.FindException;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@NotThreadSafe
public class ThrowableChain {

    private Throwable mFirstThrowable;

    ThrowableChain(Throwable top) {
        mFirstThrowable = top;
    }

    public ThrowableChain() {
        this(null);
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

    public RuntimeException toRuntime(Supplier<? extends RuntimeException> creator) {
        if (mFirstThrowable != null) {
            if (mFirstThrowable instanceof RuntimeException) {
                return (RuntimeException) mFirstThrowable;
            }

            return new RuntimeException(mFirstThrowable);
        }

        return creator.get();
    }
}