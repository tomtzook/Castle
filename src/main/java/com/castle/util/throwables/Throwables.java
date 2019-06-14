package com.castle.util.throwables;

import java.util.function.Consumer;

public class Throwables {

    private Throwables() {}

    public static Consumer<AutoCloseable> silentCloser() {
        return (closeable) -> {
            try {
                closeable.close();
            } catch (Throwable t) {}
        };
    }
}
