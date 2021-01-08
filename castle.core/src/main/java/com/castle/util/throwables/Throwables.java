package com.castle.util.throwables;

import com.castle.annotations.Stateless;

import java.util.function.Function;

@Stateless
public class Throwables {

    private Throwables() {
    }

    private static final ThrowableHandler SILENT_HANDLER = new SilentHandler();

    public static ThrowableHandler silentHandler() {
        return SILENT_HANDLER;
    }

    public static ThrowableChain newChain() {
        return new ThrowableChain();
    }

    public static <E extends Throwable> void throwIfType(Throwable throwable, Class<E> type) throws E {
        if (type.isInstance(throwable)) {
            throw type.cast(throwable);
        }
    }

    public static void throwAsRuntime(Throwable throwable) {
        if (throwable != null) {
            throw new RuntimeException(throwable);
        }
    }

    public static <E extends Throwable> void throwAsType(Throwable throwable, Class<E> type, Function<Throwable, E> converter) throws E {
        throwIfType(throwable, type);

        throw converter.apply(throwable);
    }

    public static <E extends Throwable> E getAsType(Throwable throwable, Class<E> type, Function<Throwable, E> converter) {
        if (type.isInstance(throwable)) {
            return type.cast(throwable);
        }

        return converter.apply(throwable);
    }
}
