package com.castle.concurrent.executor;

import com.castle.concurrent.service.TaskService;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

public class SingleActionExecutor extends TaskService implements ActionExecutor {

    private final AtomicReference<CompletableActionContext> mNextAction;
    private final CountDownLatch mNextActionLatch;

    private SingleActionExecutor(ExecutorService executorService, AtomicReference<CompletableActionContext> nextAction, CountDownLatch nextActionLatch) {
        super(executorService, new Task(nextAction, nextActionLatch));
        mNextAction = nextAction;
        mNextActionLatch = nextActionLatch;
    }

    @Override
    public Future<?> execute(Action action) {
        CompletableFuture<?> future = new CompletableFuture<>();
        CompletableActionContext old = mNextAction.getAndSet(new CompletableActionContext(action, future));
        if (old != null) {
            old.cancel();
        }

        mNextActionLatch.countDown();
        return future;
    }

    private static class Task implements Runnable {

        private final AtomicReference<CompletableActionContext> mNextAction;
        private final CountDownLatch mNextActionLatch;
        private CompletableActionContext mCurrentAction;

        private Task(AtomicReference<CompletableActionContext> nextAction, CountDownLatch nextActionLatch) {
            mNextAction = nextAction;
            mNextActionLatch = nextActionLatch;
            mCurrentAction = null;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    CompletableActionContext nextAction = mNextAction.getAndSet(null);
                    if (nextAction != null) {
                        mCurrentAction.cancel();
                        mCurrentAction = nextAction;
                    }

                    if (mCurrentAction != null) {
                        mCurrentAction.run();

                        if (mCurrentAction.isDone()) {
                            mCurrentAction = null;
                        }
                    } else {
                        mNextActionLatch.await();
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
