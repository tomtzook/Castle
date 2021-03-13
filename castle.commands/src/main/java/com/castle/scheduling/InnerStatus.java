package com.castle.scheduling;

import com.castle.time.Time;

public interface InnerStatus<R> extends Status<R> {

    Time getQueuedTime();

    void markStarted(Time time);
    void markFinished(R result);
    void markErrored(Throwable error);
}
