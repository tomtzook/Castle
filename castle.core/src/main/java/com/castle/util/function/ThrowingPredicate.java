package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;
import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Predicate;

@FunctionalInterface
public interface ThrowingPredicate<T, E extends Exception> {

    @FunctionalInterface
    interface Generic<T> extends ThrowingPredicate<T, FunctionException> {

    }

    boolean test(T t) throws E;

    default Predicate<T> asSilent(ThrowableHandler handler, boolean defaultResult) {
        return (t)-> {
            try {
                return test(t);
            } catch (Exception e) {
                handler.handle(e);
                return defaultResult;
            }
        };
    }
}
