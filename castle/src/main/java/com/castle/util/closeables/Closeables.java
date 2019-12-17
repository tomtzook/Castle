package com.castle.util.closeables;

import com.castle.annotations.Stateless;
import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;

import java.util.Collection;
import java.util.function.Consumer;

@Stateless
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

    public static void close(Collection<? extends AutoCloseable> closeables, ThrowableHandler handler) {
        closeables.forEach(closer(handler));
    }

    public static void silentClose(Collection<? extends AutoCloseable> closeables) {
        close(closeables, Throwables.silentHandler());
    }

    public static <T extends AutoCloseable> Consumer<T> closer(ThrowableHandler handler) {
        return (closeable) -> close(closeable, handler);
    }

    public static <T extends AutoCloseable> Consumer<T> silentCloser() {
        return closer(Throwables.silentHandler());
    }
}
