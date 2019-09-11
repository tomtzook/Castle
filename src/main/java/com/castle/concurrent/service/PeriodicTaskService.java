package com.castle.concurrent.service;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

public class PeriodicTaskService extends TerminalService {

    private final ScheduledExecutorService mExecutorService;
    private final Runnable mTask;
    private final LongSupplier mPeriodMsSupplier;

    private Future<?> mTaskFuture;

    public PeriodicTaskService(ScheduledExecutorService executorService, Runnable task, LongSupplier periodMsSupplier) {
        mExecutorService = executorService;
        mTask = task;
        mPeriodMsSupplier = periodMsSupplier;

        mTaskFuture = null;
    }

    @Override
    protected void startRunning() {
        long period = mPeriodMsSupplier.getAsLong();
        mTaskFuture = mExecutorService.scheduleAtFixedRate(mTask,
                period, period, TimeUnit.MILLISECONDS);

    }

    @Override
    protected void stopRunning() {
        if (mTaskFuture != null) {
            mTaskFuture.cancel(true);
            mTaskFuture = null;
        }
    }

    @Override
    public boolean isRunning() {
        return mTaskFuture != null && !mTaskFuture.isDone();
    }
}
