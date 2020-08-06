package com.castle.concurrent.service;

public interface TerminalService extends Service, AutoCloseable {

    @Override
    void close();
    boolean isClosed();
}
