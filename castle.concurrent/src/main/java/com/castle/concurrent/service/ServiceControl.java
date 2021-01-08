package com.castle.concurrent.service;

public class ServiceControl {

    private boolean mIsRunning;
    private boolean mIsTerminated;

    public ServiceControl() {
        mIsRunning = false;
        mIsTerminated = false;
    }

    public boolean isRunning() {
        return mIsRunning;
    }

    public boolean isTerminated() {
        return mIsTerminated;
    }

    public void ensureCanStart() {
        if (mIsRunning) {
            throw new IllegalStateException("already running");
        }
        if (mIsTerminated) {
            throw new IllegalStateException("terminated");
        }
    }

    public void markStarted() {
        mIsRunning = true;
    }

    public void ensureCanStop() {
        if (!mIsRunning) {
            throw new IllegalStateException("not running");
        }
    }

    public void markStopped() {
        mIsRunning = false;
    }

    public void markTerminated() {
        mIsTerminated = true;
    }
}
