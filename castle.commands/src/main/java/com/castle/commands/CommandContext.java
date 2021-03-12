package com.castle.commands;

import com.castle.commands.exceptions.RedoCommandException;
import com.castle.time.Clock;
import com.castle.time.Time;
import com.castle.util.dependencies.DependencyContainer;

public class CommandContext<R> {

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

    public ExecutionResult execute(DependencyContainer container) {
        if (mStatus.isCanceled()) {
            return ExecutionResult.DONE;
        }

        Time now = mClock.currentTime();
        if (mStartDelay.isValid() && !now.sub(mStatus.getStartTime()).after(mStartDelay)) {
            return ExecutionResult.REDO;
        }

        try {
            R result = mCommand.execute(container, mParameters);
            mStatus.success(result);
        } catch (RedoCommandException e) {
            return ExecutionResult.REDO;
        } catch (Throwable t) {
            mStatus.fail(t);
        }

        return ExecutionResult.DONE;
    }
}
