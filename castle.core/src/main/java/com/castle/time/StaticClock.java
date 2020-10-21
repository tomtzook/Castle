package com.castle.time;

import com.castle.annotations.Immutable;

@Immutable
public class StaticClock implements Clock {

    private final Time mTime;

    public StaticClock(Time time) {
        mTime = time;
    }

    public StaticClock() {
        this(Time.INVALID);
    }

    @Override
    public Time currentTime() {
        return mTime;
    }
}
