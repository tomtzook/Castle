package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;
import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Supplier;

@FunctionalInterface
public interface ThrowingSupplier<T, E extends Exception> {

    @FunctionalInterface
    interface Generic<T> extends ThrowingSupplier<T, FunctionException> {

    }

    T get() throws E;

    default Supplier<T> asSilent(ThrowableHandler handler, T defaultResult) {
        return ()-> {
            try {
                return get();
            } catch (Exception e) {
                handler.handle(e);
                return defaultResult;
            }
        };
    }
}
