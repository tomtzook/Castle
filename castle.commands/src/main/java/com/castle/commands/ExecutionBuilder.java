package com.castle.commands;

import com.castle.time.Time;

public interface ExecutionBuilder<R> {

    ExecutionBuilder<R> withParameters(Parameters parameters);
    ExecutionBuilder<R> withStartDelay(Time delay);

    Status<R> start();
}
