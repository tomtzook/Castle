package com.castle.concurrent.executor;

import com.castle.concurrent.service.TaskService;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class SingleThreadActionExecutor extends TaskService implements ActionExecutor {


    private final BlockingQueue<CompletableActionContext> mQueue;

    SingleThreadActionExecutor(ExecutorService executorService, BlockingQueue<CompletableActionContext> queue,
                               Function<BlockingQueue<? extends ActionContext>, Runnable> taskFactory) {
        super(executorService, taskFactory.apply(queue));
        mQueue = queue;
    }

    public SingleThreadActionExecutor(ExecutorService executorService,
                                      Function<BlockingQueue<? extends ActionContext>, Runnable> taskFactory) {
        this(executorService, new LinkedBlockingQueue<>(), taskFactory);
    }

    public SingleThreadActionExecutor(ExecutorService executorService) {
        this(executorService, new LinkedBlockingQueue<>(), SingleThreadActionExecutor.Task::new);
    }

    @Override
    public Future<?> execute(Action action) {
        CompletableFuture<?> future = new CompletableFuture<>();
        mQueue.add(new CompletableActionContext(action, future));
        return future;
    }

    private static class Task implements Runnable {

        private final BlockingQueue<? extends ActionContext> mTaskQueue;
        private ActionContext mCurrentTask;

        private Task(BlockingQueue<? extends ActionContext> taskQueue) {
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
