package com.castle.concurrent.service;

import com.castle.annotations.ThreadSafe;

@ThreadSafe
public abstract class ServiceBase implements Service {

    private final ServiceControl mServiceControl;

    protected ServiceBase(ServiceControl serviceControl) {
        mServiceControl = serviceControl;
    }

    protected ServiceBase() {
        this(new ServiceControl());
    }

    @Override
    public synchronized void start() {
        mServiceControl.ensureCanStart();
        startRunning();
        mServiceControl.markStarted();
    }

    @Override
    public synchronized void stop() {
        mServiceControl.ensureCanStop();
        stopRunning();
        mServiceControl.markStopped();
    }

    @Override
    public final boolean isRunning() {
        return mServiceControl.isRunning();
    }

    protected abstract void startRunning();
    protected abstract void stopRunning();
}
