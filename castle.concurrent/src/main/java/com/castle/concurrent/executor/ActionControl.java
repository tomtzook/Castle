package com.castle.concurrent.executor;

public interface ActionControl {

    void complete();
    void fail(Throwable t);
    void cancel();
}
