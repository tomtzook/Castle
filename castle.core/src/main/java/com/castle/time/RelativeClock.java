package com.castle.time;

import com.castle.annotations.Immutable;

@Immutable
public class RelativeClock implements Clock {

    private final Clock mOriginalClock;
    private final Time mStartTime;

    public RelativeClock(Clock originalClock, Time startTime) {
        mOriginalClock = originalClock;
        mStartTime = startTime;
    }

    public RelativeClock(Clock originalClock) {
        this(originalClock, originalClock.currentTime());
    }

    @Override
    public Time currentTime() {
        return mOriginalClock.currentTime().sub(mStartTime);
    }
}
