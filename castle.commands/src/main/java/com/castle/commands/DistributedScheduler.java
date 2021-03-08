package com.castle.commands;

import com.castle.util.dependencies.DependencyContainer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;

public class DistributedScheduler implements Scheduler {

    private final BlockingQueue<CommandContext<?>> mCommands;

    public DistributedScheduler(CommandExecutor commandExecutor, ExecutorService executorService, int workers) {
        mCommands = new LinkedBlockingQueue<>();

        for (int i = 0; i < workers; i++) {
            executorService.submit(new ExecutionTask(commandExecutor, mCommands));
        }
    }

    public DistributedScheduler(DependencyContainer dependencyContainer, ExecutorService executorService, int workers) {
        this(new CommandExecutor(dependencyContainer), executorService, workers);
    }

    @Override
    public <R> Result<R> start(Command<R> command) {
        return start(command, new Parameters());
    }

    @Override
    public <R> Result<R> start(Command<R> command, Parameters parameters) {
        InnerResult<R> result = new ResultImpl<>();
        mCommands.add(new CommandContext<>(command, parameters, result));
        return result;
    }
}
