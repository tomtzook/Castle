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
}
