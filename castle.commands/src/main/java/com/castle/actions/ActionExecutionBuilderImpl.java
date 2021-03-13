package com.castle.actions;

import com.castle.scheduling.InnerStatus;
import com.castle.scheduling.Status;
import com.castle.scheduling.StatusImpl;
import com.castle.time.Clock;
import com.castle.time.Time;

import java.util.function.Consumer;

public class ActionExecutionBuilderImpl<R> implements ActionExecutionBuilder<R> {

    private final Consumer<? super ActionContext<?>> mAddAction;
    private final Clock mClock;
    private final Action<R> mAction;

    private final Configuration mConfiguration;

    public ActionExecutionBuilderImpl(Consumer<? super ActionContext<?>> addAction, Clock clock, Action<R> action) {
        mAddAction = addAction;
        mClock = clock;
        mAction = action;

        mConfiguration = new ConfigurationImpl();
    }

    @Override
    public ActionExecutionBuilder<R> withTimeout(Time timeout) {
        mConfiguration.setTimeout(timeout);
        return this;
    }

    @Override
    public Status<R> start() {
        InnerStatus<R> status = new StatusImpl<>(mClock.currentTime());
        mAddAction.accept(new ActionContext<>(mClock, mAction, status, mConfiguration));
        return status;
    }
}
