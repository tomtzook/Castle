package com.castle.commands;

import com.castle.util.dependencies.DependencyContainer;

public class CommandExecutor {

    private final DependencyContainer mDependencyContainer;

    public CommandExecutor(DependencyContainer dependencyContainer) {
        mDependencyContainer = dependencyContainer;
    }

    public ExecutionResult execute(CommandContext<?> commandContext) {
        return commandContext.execute(mDependencyContainer);
    }
}
