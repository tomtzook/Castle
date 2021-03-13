package com.castle.scheduling;

import com.castle.actions.Action;
import com.castle.actions.ActionExecutionBuilder;
import com.castle.actions.ActionExecutionBuilderImpl;
import com.castle.commands.Command;
import com.castle.commands.CommandExecutionBuilder;
import com.castle.commands.CommandExecutionBuilderImpl;
import com.castle.time.Clock;
import com.castle.util.dependencies.DependencyContainer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class DistributedScheduler implements Scheduler {

    private final BlockingQueue<ExecutionContext> mExecutions;
    private final Clock mClock;
    private final Consumer<ExecutionContext> mAddExecution;

    public DistributedScheduler(DependencyContainer dependencyContainer, ExecutorService executorService, int workers,
                                Clock clock) {
        mClock = clock;
        mExecutions = new LinkedBlockingQueue<>();
        mAddExecution = mExecutions::add;

        for (int i = 0; i < workers; i++) {
            executorService.submit(new ExecutionTask(mExecutions, dependencyContainer));
        }
    }

    public DistributedScheduler(DependencyContainer dependencyContainer,
                                ExecutorService executorService, int workers) {
        this(dependencyContainer, executorService, workers, dependencyContainer.get(Clock.class));
    }

    @Override
    public <R> CommandExecutionBuilder<R> submit(Command<R> command) {
        return new CommandExecutionBuilderImpl<>(mAddExecution, mClock, command);
    }

    @Override
    public <R> ActionExecutionBuilder<R> submit(Action<R> action) {
        return new ActionExecutionBuilderImpl<>(mAddExecution, mClock, action);
    }
}
