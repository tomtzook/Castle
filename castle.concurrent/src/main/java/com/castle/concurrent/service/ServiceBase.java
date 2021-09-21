package com.castle.concurrent.service;

import com.castle.annotations.ThreadSafe;
import com.castle.exceptions.ServiceException;

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
    public synchronized void start() throws ServiceException {
        if (!isRunning()) {
            mServiceControl.markStopped();
        }

        mServiceControl.ensureCanStart();
        startRunning();
        mServiceControl.markStarted();
    }

    @Override
    public synchronized void stop() {
        if (!isRunning()) {
            mServiceControl.markStopped();
        }

        mServiceControl.ensureCanStop();
        stopRunning();
        mServiceControl.markStopped();
    }

    @Override
    public boolean isRunning() {
        return mServiceControl.isRunning();
    }

    protected abstract void startRunning() throws ServiceException;

    protected abstract void stopRunning();
}
