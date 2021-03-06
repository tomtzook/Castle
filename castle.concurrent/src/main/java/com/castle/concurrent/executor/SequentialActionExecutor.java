package com.castle.concurrent.executor;

import com.castle.concurrent.service.TaskService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SequentialActionExecutor extends TaskService implements ActionExecutor {


    private final BlockingQueue<CompletableActionContext> mQueue;

    SequentialActionExecutor(ExecutorService executorService, BlockingQueue<CompletableActionContext> queue,
                             Function<BlockingQueue<CompletableActionContext>, Runnable> taskFactory) {
        super(executorService, taskFactory.apply(queue));
        mQueue = queue;
    }

    public SequentialActionExecutor(ExecutorService executorService,
                                    Function<BlockingQueue<CompletableActionContext>, Runnable> taskFactory) {
        this(executorService, new LinkedBlockingQueue<>(), taskFactory);
    }

    public SequentialActionExecutor(ExecutorService executorService) {
        this(executorService, new LinkedBlockingQueue<>(), SequentialActionExecutor.Task::new);
    }

    @Override
    public Future<?> execute(Action action) {
        CompletableFuture<?> future = new CompletableFuture<>();
        mQueue.add(new CompletableActionContext(action, future));
        return future;
    }

    private static class Task implements Runnable {

        private final BlockingQueue<CompletableActionContext> mTaskQueue;
        private CompletableActionContext mCurrentTask;

        private Task(BlockingQueue<CompletableActionContext> taskQueue) {
            mTaskQueue = taskQueue;
            mCurrentTask = null;
        }

        @Override
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    while (mCurrentTask == null) {
                        mCurrentTask = mTaskQueue.poll(0, TimeUnit.MILLISECONDS);
                    }

                    mCurrentTask.run();

                    if (mCurrentTask.isDone()) {
                        mCurrentTask = null;
                    }
                }
            } catch (InterruptedException e) {
            }
        }
    }
}
