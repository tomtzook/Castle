package com.castle.commands;

import com.castle.time.Time;

public interface Status<R> {

    boolean isDone();
    boolean didSucceed();
    boolean isCanceled();

    Time getStartTime();

    R getResult();
    Throwable getError();

    void cancel();
}
