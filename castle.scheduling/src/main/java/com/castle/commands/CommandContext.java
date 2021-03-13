package com.castle.commands;

import com.castle.commands.exceptions.RedoCommandException;
import com.castle.scheduling.ExecutionContext;
import com.castle.scheduling.InnerStatus;
import com.castle.time.Clock;
import com.castle.time.Time;
import com.castle.util.dependencies.DependencyContainer;

public class CommandContext<R> implements ExecutionContext {

    private final Clock mClock;
    private final Command<R> mCommand;
    private final Parameters mParameters;
    private final Time mStartDelay;
    private final InnerStatus<R> mStatus;

    public CommandContext(Clock clock, Command<R> command,
                          Parameters parameters, Time delay, InnerStatus<R> status) {
        mClock = clock;
        mCommand = command;
        mParameters = parameters;
        mStartDelay = delay;
        mStatus = status;
    }

    @Override
    public boolean execute(DependencyContainer container) {
        if (mStatus.isCanceled()) {
            return true;
        }

        if (mStatus.isPending()) {
            Time now = mClock.currentTime();
            if (mStartDelay.isValid() && !now.sub(mStatus.getQueuedTime()).after(mStartDelay)) {
                return false;
            }

            mStatus.markStarted(now);
        }

        try {
            R result = mCommand.execute(container, mParameters);
            mStatus.markFinished(result);
        } catch (RedoCommandException e) {
            return false;
        } catch (Throwable t) {
            mStatus.markErrored(t);
        }

        return true;
    }
}
