package com.castle.commands;

public interface Scheduler {

    <R> Result<R> start(Command<R> command);
    <R> Result<R> start(Command<R> command, Parameters parameters);
}
