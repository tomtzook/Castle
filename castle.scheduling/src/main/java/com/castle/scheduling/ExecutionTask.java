package com.castle.scheduling;

import com.castle.scheduling.ExecutionContext;
import com.castle.util.dependencies.DependencyContainer;

import java.util.concurrent.BlockingQueue;

public class ExecutionTask implements Runnable {

    private final BlockingQueue<ExecutionContext> mQueue;
    private final DependencyContainer mDependencyContainer;

    public ExecutionTask(BlockingQueue<ExecutionContext> queue, DependencyContainer dependencyContainer) {
        mQueue = queue;
        mDependencyContainer = dependencyContainer;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            try {
                ExecutionContext context = mQueue.take();
                boolean isFinished = context.execute(mDependencyContainer);
                if (!isFinished) {
                    mQueue.add(context);
                }
            } catch (InterruptedException e) {
                break;
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }
}
