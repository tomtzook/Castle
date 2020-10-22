package com.castle.concurrent.executor;

import java.util.concurrent.CompletableFuture;

public class CompletableActionControl implements ActionControl {

    private final CompletableFuture<?> mFuture;

    public CompletableActionControl(CompletableFuture<?> future) {
        mFuture = future;
    }

    @Override
    public void complete() {
        mFuture.complete(null);
    }

    @Override
    public void fail(Throwable t) {
        mFuture.completeExceptionally(t);
    }

    @Override
    public void cancel() {
        mFuture.cancel(true);
    }
}
