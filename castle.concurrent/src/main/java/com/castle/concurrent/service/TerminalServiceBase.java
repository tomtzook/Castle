package com.castle.concurrent.service;

import com.castle.annotations.ThreadSafe;

@ThreadSafe
public abstract class TerminalServiceBase extends ServiceBase implements TerminalService {

    private final ServiceControl mServiceControl;

    protected TerminalServiceBase(ServiceControl serviceControl) {
        super(serviceControl);
        mServiceControl = serviceControl;
    }

    protected TerminalServiceBase() {
        this(new ServiceControl());
    }

    @Override
    public synchronized void close() {
        if (isRunning()) {
            stop();
        }

        mServiceControl.markTerminated();
    }

    @Override
    public final boolean isClosed() {
        return mServiceControl.isTerminated();
    }
}
