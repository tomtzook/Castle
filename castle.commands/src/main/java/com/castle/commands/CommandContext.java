package com.castle.commands;

import com.castle.util.dependencies.DependencyContainer;

public class CommandContext<R> {

    private final Command<R> mCommand;
    private final Parameters mParameters;
    private final InnerResult<R> mResult;

    public CommandContext(Command<R> command, Parameters parameters, InnerResult<R> result) {
        mCommand = command;
        mParameters = parameters;
        mResult = result;
    }

    public ExecutionResult execute(DependencyContainer container) {
        if (mResult.isCanceled()) {
            return ExecutionResult.DONE;
        }

        try {
            R result = mCommand.execute(container, mParameters);
            mResult.success(result);
        } catch (Throwable t) {
            mResult.fail(t);
        }

        return ExecutionResult.DONE;
    }
}
