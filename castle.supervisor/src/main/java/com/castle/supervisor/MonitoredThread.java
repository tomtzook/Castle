package com.castle.supervisor;

import java.util.Set;

public interface MonitoredThread extends ThreadState {

    Set<ThreadOption> options();

    void start();
    void stop();
}
