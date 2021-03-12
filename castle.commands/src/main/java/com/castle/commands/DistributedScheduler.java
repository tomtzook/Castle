package com.castle.commands;

import com.castle.time.Clock;
import com.castle.util.dependencies.DependencyContainer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class DistributedScheduler implements Scheduler {

    private final BlockingQueue<CommandContext<?>> mCommands;
    private final Clock mClock;

    public DistributedScheduler(CommandExecutor commandExecutor, ExecutorService executorService, int workers,
                                Clock clock) {
        mClock = clock;
        mCommands = new LinkedBlockingQueue<>();

        for (int i = 0; i < workers; i++) {
            executorService.submit(new ExecutionTask(commandExecutor, mCommands));
        }
    }

    public DistributedScheduler(DependencyContainer dependencyContainer,
                                ExecutorService executorService, int workers) {
        this(new CommandExecutor(dependencyContainer), executorService, workers, dependencyContainer.get(Clock.class));
    }

    @Override
    public <R> ExecutionBuilder<R> submit(Command<R> command) {
        return new ExecutionBuilderImpl<>(mCommands, mClock, command);
    }
}
