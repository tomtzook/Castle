package com.castle.util;

import java.io.Closeable;
import java.util.Collection;

public class Closeables {

    private Closeables() {}

    public static void closeQuietly(Collection<? extends Closeable> closeables) {
        closeables.forEach(Closeables::closeQuietly);
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            closeable.close();
        } catch (Throwable t) { }
    }
}
