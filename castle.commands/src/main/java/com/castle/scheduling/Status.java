package com.castle.scheduling;

import com.castle.time.Time;

public interface Status<R> {

    boolean isPending();
    boolean isDone();
    boolean isSuccessful();
    boolean isErrored();
    boolean isCanceled();

    Time getStartTime();

    R getResult();
    Throwable getError();

    void cancel();
}
