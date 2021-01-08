package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;
import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Consumer;

@FunctionalInterface
public interface ThrowingConsumer<T, E extends Exception> {

    @FunctionalInterface
    interface Generic<T> extends ThrowingConsumer<T, FunctionException> {

    }

    void accept(T t) throws E;

    default Consumer<T> asSilent(ThrowableHandler handler) {
        return (t)-> {
            try {
                accept(t);
            } catch (Exception e) {
                handler.handle(e);
            }
        };
    }
}
