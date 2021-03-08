package com.castle.commands;

public interface InnerResult<R> extends Result<R> {

    void success(R result);
    void fail(Throwable cause);
}
