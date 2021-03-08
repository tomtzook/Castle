package com.castle.commands;

import com.castle.commands.exceptions.CommandException;
import com.castle.util.dependencies.DependencyContainer;

public interface Command<R> {

    R execute(DependencyContainer dependencyContainer, Parameters parameters) throws CommandException;
}
