package com.castle.concurrent.service;

public abstract class TerminalService extends ServiceBase {

    private volatile boolean mIsTerminated;

    protected TerminalService() {
        mIsTerminated = false;
    }

    public boolean isTerminated() {
        return mIsTerminated;
    }
    
    @Override
    public synchronized void start() {
        if (mIsTerminated) {
            throw new IllegalStateException("terminated");
        }
        super.start();
    }

    protected void markTerminated() {
        mIsTerminated = true;
    }
}
