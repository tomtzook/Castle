package com.castle.util.throwables;

public class Throwables {

    private Throwables() {}

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
}
