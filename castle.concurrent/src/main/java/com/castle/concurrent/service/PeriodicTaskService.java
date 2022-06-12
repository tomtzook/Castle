package com.castle.concurrent.service;

import com.castle.annotations.ThreadSafe;
import com.castle.time.Time;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

@ThreadSafe
public class PeriodicTaskService extends TerminalServiceBase {

    private final ScheduledExecutorService mExecutorService;
    private final Runnable mTask;
    private final Supplier<Time> mPeriodSupplier;

    private Future<?> mTaskFuture;

    public PeriodicTaskService(ScheduledExecutorService executorService, Runnable task, Supplier<Time> periodSupplier) {
        mExecutorService = executorService;
        mTask = task;
        mPeriodSupplier = periodSupplier;

        mTaskFuture = null;
    }

    @Override
    public boolean isRunning() {
        return super.isRunning() &&
                mTaskFuture != null &&
                !mTaskFuture.isDone();
    }

    @Override
    protected void startRunning() {
        Time period = mPeriodSupplier.get();
        mTaskFuture = mExecutorService.scheduleAtFixedRate(mTask,
                period.value(), period.value(), period.unit());

    }

    @Override
    protected void stopRunning() {
        if (mTaskFuture != null) {
            mTaskFuture.cancel(true);
            mTaskFuture = null;
        }
    }
}
