package com.castle.actions;

import com.castle.time.Time;

public interface Control<R> {

    Time getStartTime();

    boolean wasInterrupted();

    void finished();
    void finished(R result);
}
