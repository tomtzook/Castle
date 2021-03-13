package com.castle.scheduling;

import com.castle.actions.Action;
import com.castle.actions.ActionExecutionBuilder;
import com.castle.commands.Command;
import com.castle.commands.CommandExecutionBuilder;

public interface Scheduler {

    <R> CommandExecutionBuilder<R> submit(Command<R> command);
    <R> ActionExecutionBuilder<R> submit(Action<R> action);
}
