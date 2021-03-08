package com.castle.commands;

public interface Result<R> {

    boolean isDone();
    boolean didSucceed();
    boolean isCanceled();

    R get();
    Throwable getError();

    void cancel();
}
