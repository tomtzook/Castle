package com.castle.commands;

public interface InnerStatus<R> extends Status<R> {

    void success(R result);
    void fail(Throwable cause);
}
