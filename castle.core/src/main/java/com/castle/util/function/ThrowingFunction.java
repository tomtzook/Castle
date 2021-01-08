package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;
import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Function;

@FunctionalInterface
public interface ThrowingFunction<T, R, E extends Exception> {

    @FunctionalInterface
    interface Generic<T, R> extends ThrowingFunction<T, R, FunctionException> {

    }

    R apply(T t) throws E;

    default Function<T, R> asSilent(ThrowableHandler handler, R defaultResult) {
        return (t)-> {
            try {
                return apply(t);
            } catch (Exception e) {
                handler.handle(e);
                return defaultResult;
            }
        };
    }
}
