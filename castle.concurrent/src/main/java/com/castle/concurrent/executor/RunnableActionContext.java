package com.castle.concurrent.executor;

public class RunnableActionContext implements ActionContext {

    private final Action mAction;
    private boolean mIsDone;

    public RunnableActionContext(Action action) {
        mAction = action;

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

        mAction.initialize();

        while (!mIsDone && !Thread.interrupted()) {
            mIsDone = mAction.execute();
        }
    }
}
