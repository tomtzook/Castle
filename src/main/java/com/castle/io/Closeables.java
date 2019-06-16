package com.castle.io;

import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;

import java.util.function.Consumer;

public class Closeables {

    private Closeables() {}

    public static <T extends AutoCloseable> void close(T closeable, ThrowableHandler handler) {
        try {
            closeable.close();
        } catch (Throwable t) {
            handler.handle(t);
        }
    }

    public static <T extends AutoCloseable> void silentClose(T closeable) {
        close(closeable, Throwables.silentHandler());
    }

    public static <T extends AutoCloseable> Consumer<T> closer(ThrowableHandler handler) {
        return (closeable) -> close(closeable, handler);
    }

    public static <T extends AutoCloseable> Consumer<T> silentCloser() {
        return closer(Throwables.silentHandler());
    }
}
