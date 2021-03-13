package com.castle.actions;

public interface InnerControl<R> extends Control<R> {

    boolean isFinished();

    void markInterrupted();

    R getResult();
}
