package com.castle.concurrent.service;

public abstract class ServiceBase implements Service {

    @Override
    public synchronized void start() {
        if (isRunning()) {
            throw new IllegalStateException("already running");
        }

        startRunning();
    }

    @Override
    public synchronized void stop() {
        if (!isRunning()) {
            throw new IllegalStateException("not running");
        }

        stopRunning();
    }

    protected abstract void startRunning();
    protected abstract void stopRunning();
}
