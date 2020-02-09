package com.castle.concurrent.service;

public interface TerminalService extends Service {

    void terminate();

    boolean isTerminated();
}
