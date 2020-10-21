package com.castle.time;

import com.castle.annotations.Immutable;

import java.util.concurrent.TimeUnit;

@Immutable
public class SystemNanoClock implements Clock {

    private final long mStartTimeNanos;

    /**
     * @deprecated Use {@link Clocks#systemNanosClock()} instead.
     */
    @Deprecated
    public SystemNanoClock() {
        this(false);
    }

    /**
     * So that internal creations of this won't report deprecation
     *
     * @param deprecationOverride ignored
     */
    SystemNanoClock(boolean deprecationOverride) {
        mStartTimeNanos = System.nanoTime();
    }

    @Override
    public Time currentTime() {
        long timeNanos = System.nanoTime() - mStartTimeNanos;
        return new Time(timeNanos, TimeUnit.NANOSECONDS);
    }
}
