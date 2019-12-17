package com.castle.concurrent.service;

public interface Service {

    void start();
    void stop();

    boolean isRunning();
}
