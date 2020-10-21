package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

    @FunctionalInterface
    interface Generic<T, R> extends ThrowingFunction<T, R, FunctionException> {

    }

    R apply(T t) throws E;
}
