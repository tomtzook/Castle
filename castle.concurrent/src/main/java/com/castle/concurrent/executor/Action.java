package com.castle.concurrent.executor;

public interface Action {

    void initialize(ActionControl control) throws Exception;
    void execute(ActionControl control) throws Exception;
    void done();
}
