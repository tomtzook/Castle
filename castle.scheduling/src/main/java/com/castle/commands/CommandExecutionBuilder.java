package com.castle.commands;

import com.castle.scheduling.Status;
import com.castle.time.Time;

public interface CommandExecutionBuilder<R> {

    CommandExecutionBuilder<R> withParameters(Parameters parameters);
    CommandExecutionBuilder<R> withStartDelay(Time delay);

    Status<R> start();
}
