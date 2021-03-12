package com.castle.commands;

import com.castle.time.Clock;
import com.castle.time.Time;

import java.util.Collection;

public class ExecutionBuilderImpl<R> implements ExecutionBuilder<R> {

    private final Collection<CommandContext<?>> mCommands;
    private final Clock mClock;
    private final Command<R> mCommand;

    private Parameters mParameters;
    private Time mStartDelay;

    public ExecutionBuilderImpl(Collection<CommandContext<?>> commands, Clock clock, Command<R> command) {
        mCommands = commands;
        mClock = clock;
        mCommand = command;

        mParameters = new Parameters();
        mStartDelay = Time.INVALID;
    }

    @Override
    public ExecutionBuilder<R> withParameters(Parameters parameters) {
        mParameters = parameters;
        return this;
    }

    @Override
    public ExecutionBuilder<R> withStartDelay(Time delay) {
        mStartDelay = delay;
        return this;
    }

    @Override
    public Status<R> start() {
        InnerStatus<R> status = new StatusImpl<>(mClock.currentTime());
        mCommands.add(new CommandContext<>(mClock, mCommand, mParameters, mStartDelay, status));
        return status;
    }
}
