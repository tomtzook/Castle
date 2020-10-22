package com.castle.concurrent.executor;

public interface Action {

    void initialize();
    boolean execute();
}
