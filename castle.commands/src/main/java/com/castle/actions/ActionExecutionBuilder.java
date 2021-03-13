package com.castle.actions;

import com.castle.scheduling.Status;
import com.castle.time.Time;

public interface ActionExecutionBuilder<R> {

    ActionExecutionBuilder<R> withTimeout(Time timeout);

    Status<R> start();
}
