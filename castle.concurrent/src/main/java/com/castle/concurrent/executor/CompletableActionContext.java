package com.castle.concurrent.executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class CompletableActionContext {

    private final Action mAction;
    private final Future<?> mFuture;
    private final ActionControl mActionControl;

    private boolean mIsInitialized;

    private CompletableActionContext(Action action, Future<?> future, ActionControl actionControl) {
        mAction = action;
        mFuture = future;
        mActionControl = actionControl;

        mIsInitialized = false;
    }

    public CompletableActionContext(Action action, CompletableFuture<?> completableFuture) {
        this(action, completableFuture, new CompletableActionControl(completableFuture));
    }

    public boolean isDone() {
        return mFuture.isDone() || mFuture.isCancelled();
    }

    public void run() {
        if (isDone()) {
            return;
        }

        try {
            if (!mIsInitialized) {
                mAction.initialize(mActionControl);
                mIsInitialized = true;

                if (isDone()) {
                    mAction.done();
                    return;
                }
            }

            mAction.execute(mActionControl);
            if (isDone()) {
                mAction.done();
            }
        } catch (Throwable t) {
            mActionControl.fail(t);
            mAction.done();
        }
    }

    public void cancel() {
        mFuture.cancel(true);
    }
}
