package com.castle.concurrent.service;

import com.castle.exceptions.ServiceException;

import java.util.concurrent.ThreadFactory;

public class ThreadService extends TerminalServiceBase {

    private final ThreadFactory mThreadFactory;
    private final Runnable mTask;

    private Thread mRunningThread;

    public ThreadService(ThreadFactory threadFactory, Runnable task) {
        mThreadFactory = threadFactory;
        mTask = task;
    }

    @Override
    public boolean isRunning() {
        return super.isRunning() &&
                mRunningThread != null &&
                mRunningThread.isAlive();
    }

    @Override
    protected void startRunning() throws ServiceException {
        mRunningThread = mThreadFactory.newThread(mTask);
    }

    @Override
    protected void stopRunning() {
        if (mRunningThread != null) {

            mRunningThread.interrupt();
            try {
                mRunningThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            mRunningThread = null;
        }
    }
}
