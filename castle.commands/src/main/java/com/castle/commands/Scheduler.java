package com.castle.commands;

public interface Scheduler {

    <R> ExecutionBuilder<R> submit(Command<R> command);
}
