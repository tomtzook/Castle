package com.castle.actions;

public interface Action<R> {

    void configure(Configuration configuration);

    void initialize(Control<R> control);
    void execute(Control<R> control);
    void end(Control<R> status);
}
