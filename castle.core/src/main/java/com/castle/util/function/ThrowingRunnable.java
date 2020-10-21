package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;

@FunctionalInterface
public interface ThrowingRunnable<R, E extends Exception> {

    @FunctionalInterface
    interface Generic<R> extends ThrowingRunnable<R, FunctionException> {

    }

    R run() throws E;
}
