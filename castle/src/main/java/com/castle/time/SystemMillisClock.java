package com.castle.time;

import java.util.concurrent.TimeUnit;

public class SystemMillisClock implements Clock {

    @Override
    public Time currentTime() {
        return new Time(System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }
}
