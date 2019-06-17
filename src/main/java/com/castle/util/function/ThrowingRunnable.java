package com.castle.util.function;

@FunctionalInterface
public interface ThrowingRunnable<R, E extends Exception> {

    R run() throws E;
}
