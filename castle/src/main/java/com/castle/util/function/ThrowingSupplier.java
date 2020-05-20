package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

    @FunctionalInterface
    interface Generic<T> extends ThrowingSupplier<T, FunctionException> {
    }

    T get() throws E;
}
