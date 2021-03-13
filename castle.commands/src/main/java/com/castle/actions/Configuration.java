package com.castle.actions;

import com.castle.time.Time;

public interface Configuration {

    Time getTimeout();
    void setTimeout(Time timeout);
}
