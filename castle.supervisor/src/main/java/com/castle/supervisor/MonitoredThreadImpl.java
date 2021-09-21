package com.castle.supervisor;

import java.util.Set;
import java.util.concurrent.ThreadFactory;

public class MonitoredThreadImpl implements MonitoredThread {

    private final ThreadFactory mThreadFactory;
    private final Runnable mTask;
    private final Set<ThreadOption> mOptions;

    private Thread mRunningThread;

    public MonitoredThreadImpl(ThreadFactory threadFactory, Runnable task, Set<ThreadOption> options) {
        mThreadFactory = threadFactory;
        mTask = task;
        mOptions = options;

        mRunningThread = null;
    }

    @Override
    public Set<ThreadOption> options() {
        return mOptions;
    }

    @Override
    public void start() {
        if (isRunning()) {
            return;
        }

        mRunningThread = mThreadFactory.newThread(mTask);
    }

    @Override
    public void stop() {
        if (mRunningThread != null) {
            if (!mRunningThread.isAlive()) {
                return;
            }

            Thread thread = mRunningThread;

            mRunningThread.interrupt();
            mRunningThread = null;

            try {
                mRunningThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean isRunning() {
        return mRunningThread != null &&
                mRunningThread.isAlive();
    }
}
