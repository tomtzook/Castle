package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

    @FunctionalInterface
    interface Generic<T> extends ThrowingConsumer<T, FunctionException> {

    }

    void accept(T t) throws E;
}
