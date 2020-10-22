package com.castle.concurrent.executor;

public class PeriodicActionContext implements ActionContext {

    private final Action mAction;
    private boolean mIsInitialized;
    private boolean mIsDone;

    public PeriodicActionContext(Action action) {
        mAction = action;

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
    }
}
