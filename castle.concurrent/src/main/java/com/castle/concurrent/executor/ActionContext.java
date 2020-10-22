package com.castle.concurrent.executor;

public interface ActionContext extends Runnable {

    boolean isDone();
}
