package com.castle.time;

import java.util.concurrent.TimeUnit;

public class SystemMillisClock implements Clock {

    /**
     * @deprecated Use {@link Clocks#systemMillisClock()} instead.
     */
    @Deprecated
    public SystemMillisClock() {
    }

    /**
     * So that internal creations of this won't report deprecation
     * @param deprecationOverride ignored
     */
    SystemMillisClock(boolean deprecationOverride) {
    }

    @Override
    public Time currentTime() {
        return new Time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
