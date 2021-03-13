package com.castle.scheduling;

import com.castle.time.Time;

import java.util.concurrent.atomic.AtomicReference;

public class StatusImpl<R> implements InnerStatus<R> {

    enum Type {
        PENDING(false),
        RUNNING(false),
        SUCCESSFUL(true),
        ERRORED(true),
        CANCELED(true)
        ;

        private final boolean mIsDone;

        Type(boolean isDone) {
            mIsDone = isDone;
        }

        public boolean isDone() {
            return mIsDone;
        }
    }

    private final Time mQueuedTime;

    private final AtomicReference<Type> mStatus;

    private Time mStartTime;
    private R mResult;
    private Throwable mError;

    public StatusImpl(Time queuedTime) {
        mQueuedTime = queuedTime;

        mStatus = new AtomicReference<>(Type.PENDING);
        mStartTime = Time.INVALID;
        mResult = null;
        mError = null;
    }

    @Override
    public Time getQueuedTime() {
        return mQueuedTime;
    }

    @Override
    public void markStarted(Time time) {
        mStartTime = time;
        mStatus.compareAndSet(Type.PENDING, Type.RUNNING);
    }

    @Override
    public void markFinished(R result) {
        mResult = result;
        mStatus.compareAndSet(Type.RUNNING, Type.SUCCESSFUL);
    }

    @Override
    public void markErrored(Throwable error) {
        mError = error;
        mStatus.compareAndSet(Type.RUNNING, Type.ERRORED);
    }

    @Override
    public boolean isPending() {
        return mStatus.get() == Type.PENDING;
    }

    @Override
    public boolean isDone() {
        return mStatus.get().isDone();
    }

    @Override
    public boolean isSuccessful() {
        return mStatus.get() == Type.SUCCESSFUL;
    }

    @Override
    public boolean isErrored() {
        return mStatus.get() == Type.ERRORED;
    }

    @Override
    public boolean isCanceled() {
        return mStatus.get() == Type.CANCELED;
    }

    @Override
    public Time getStartTime() {
        return mStartTime;
    }

    @Override
    public R getResult() {
        Type type = mStatus.get();
        if (type != Type.SUCCESSFUL) {
            throw new IllegalStateException("not successful. Status=" + type);
        }
        return mResult;
    }

    @Override
    public Throwable getError() {
        Type type = mStatus.get();
        if (type != Type.ERRORED) {
            throw new IllegalStateException("not errored. Status=" + type);
        }
        return mError;
    }

    @Override
    public void cancel() {
        mStatus.compareAndSet(Type.PENDING, Type.CANCELED);
        mStatus.compareAndSet(Type.RUNNING, Type.CANCELED);
    }

    @Override
    public String toString() {
        Type type = mStatus.get();
        switch (type) {
            case PENDING:
                return String.format("Status{PENDING, QUEUED_TIME=%s}", mQueuedTime);
            case RUNNING:
                return String.format("Status{RUNNING, START_TIME=%s}", mStartTime);
            case SUCCESSFUL:
                return String.format("Status{SUCCESSFUL, RESULT=%s}", type);
            case ERRORED:
                return String.format("Status{ERRORED, ERROR=%s: %s}",
                        mError.getClass().getName(),
                        mError.getMessage());
            case CANCELED:
                return "Status{CANCELED}";
            default:
                throw new AssertionError("unknown status " + type);
        }
    }
}
