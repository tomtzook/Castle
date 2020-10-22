package com.castle.concurrent.executor;

import java.util.concurrent.CompletableFuture;

public class CompletableActionContext implements ActionContext {

    private final Action mAction;
    private final CompletableFuture<?> mFuture;
    private boolean mIsInitialized;
    private boolean mIsDone;

    public CompletableActionContext(Action action, CompletableFuture<?> future) {
        mAction = action;
        mFuture = future;

        mIsInitialized = false;
        mIsDone = false;
    }

    @Override
    public boolean isDone() {
        return mIsDone;
    }

    @Override
    public void run() {
        if (mIsDone) {
            return;
        }

        if (!mIsInitialized) {
            mAction.initialize();
            mIsInitialized = true;
        }

        mIsDone = mAction.execute();
        if (mIsDone) {
            mFuture.complete(null);
        }
    }
}
