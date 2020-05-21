package com.castle.time;

import com.castle.annotations.Stateless;

import java.util.concurrent.TimeUnit;

@Stateless
public class UnitComparing {

    private UnitComparing() {}

    public static TimeUnit smallerUnit(TimeUnit unit1, TimeUnit unit2) {
        if (unit2.toNanos(1) < unit1.toNanos(1)) {
            return unit2;
        }

        return unit1;
    }
}
