package com.castle.util.function;

import com.castle.util.function.exceptions.FunctionException;
import com.castle.util.throwables.ThrowableHandler;

@FunctionalInterface
public interface ThrowingRunnable<E extends Exception> {

    @FunctionalInterface
    interface Generic<R> extends ThrowingRunnable<FunctionException> {

    }

    void run() throws E;

    default Runnable asSilent(ThrowableHandler handler) {
        return ()-> {
            try {
                run();
            } catch (Exception e) {
                handler.handle(e);
            }
        };
    }
}
