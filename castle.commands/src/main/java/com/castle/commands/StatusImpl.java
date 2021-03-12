package com.castle.commands;

import com.castle.time.Time;

import java.util.concurrent.atomic.AtomicReference;

public class StatusImpl<R> implements InnerStatus<R> {

    private final Time mStartTime;
    private final AtomicReference<ExecutionStatus> mStatus;
    private final AtomicReference<R> mResult;
    private final AtomicReference<Throwable> mError;

    public StatusImpl(Time startTime) {
        mStartTime = startTime;

        mStatus = new AtomicReference<>(ExecutionStatus.PENDING);
        mResult = new AtomicReference<>(null);
        mError = new AtomicReference<>(null);
    }

    @Override
    public boolean isDone() {
        return mStatus.get() != ExecutionStatus.PENDING;
    }

    @Override
    public boolean didSucceed() {
        return mStatus.get() == ExecutionStatus.SUCCESSFUL;
    }

    @Override
    public boolean isCanceled() {
        return mStatus.get() == ExecutionStatus.CANCELED;
    }

    @Override
    public Time getStartTime() {
        return mStartTime;
    }

    @Override
    public R getResult() {
        return mResult.get();
    }

    @Override
    public Throwable getError() {
        return mError.get();
    }

    @Override
    public void cancel() {
        mStatus.compareAndSet(ExecutionStatus.PENDING, ExecutionStatus.CANCELED);
    }

    @Override
    public void success(R result) {
        mResult.set(result);
        mStatus.compareAndSet(ExecutionStatus.PENDING, ExecutionStatus.SUCCESSFUL);
    }

    @Override
    public void fail(Throwable cause) {
        mError.set(cause);
        mStatus.compareAndSet(ExecutionStatus.PENDING, ExecutionStatus.FAILED);
    }

    @Override
    public String toString() {
        ExecutionStatus status = mStatus.get();
        switch (status) {
            case SUCCESSFUL:
                return String.format("Result{SUCCESSFUL, RESULT=%s}", mResult.get());
            case FAILED:
                return String.format("Result{FAILED, ERROR=%s: %s}",
                        mError.get().getClass().getName(),
                        mError.get().getMessage());
            default:
                return String.format("Result{%s, START_TIME=%s}", status.name(), mStartTime);
        }
    }
}
