package com.castle.commands;

import com.castle.scheduling.InnerStatus;
import com.castle.scheduling.Status;
import com.castle.scheduling.StatusImpl;
import com.castle.time.Clock;
import com.castle.time.Time;

import java.util.function.Consumer;

public class CommandExecutionBuilderImpl<R> implements CommandExecutionBuilder<R> {

    private final Consumer<? super CommandContext<?>> mAddCommand;
    private final Clock mClock;
    private final Command<R> mCommand;

    private Parameters mParameters;
    private Time mStartDelay;

    public CommandExecutionBuilderImpl(Consumer<? super CommandContext<?>> addCommand, Clock clock, Command<R> command) {
        mAddCommand = addCommand;
        mClock = clock;
        mCommand = command;

        mParameters = new Parameters();
        mStartDelay = Time.INVALID;
    }

    @Override
    public CommandExecutionBuilder<R> withParameters(Parameters parameters) {
        mParameters = parameters;
        return this;
    }

    @Override
    public CommandExecutionBuilder<R> withStartDelay(Time delay) {
        mStartDelay = delay;
        return this;
    }

    @Override
    public Status<R> start() {
        InnerStatus<R> status = new StatusImpl<>(mClock.currentTime());
        mAddCommand.accept(new CommandContext<>(mClock, mCommand, mParameters, mStartDelay, status));
        return status;
    }
}
