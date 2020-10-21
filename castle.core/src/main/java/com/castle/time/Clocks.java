package com.castle.time;

import com.castle.annotations.ThreadSafe;

@ThreadSafe
public class Clocks {

    private static volatile Clock sSystemMillisClock;
    private static volatile Clock sSystemNanoClock;

    private Clocks() {
    }

    public static Clock systemMillisClock() {
        if (sSystemMillisClock == null) {
            synchronized (Clocks.class) {
                if (sSystemMillisClock == null) {
                    sSystemMillisClock = new SystemMillisClock(false);
                }
            }
        }

        return sSystemMillisClock;
    }

    public static Clock systemNanosClock() {
        if (sSystemNanoClock == null) {
            synchronized (Clocks.class) {
                if (sSystemNanoClock == null) {
                    sSystemNanoClock = new SystemNanoClock(false);
                }
            }
        }

        return sSystemNanoClock;
    }
}
