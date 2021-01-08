package com.castle.concurrent.service;

import com.castle.exceptions.ServiceException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class TaskService extends TerminalServiceBase {

    private final ExecutorService mExecutorService;
    private final Runnable mTask;

    private Future<?> mTaskFuture;

    public TaskService(ExecutorService executorService, Runnable task) {
        mExecutorService = executorService;
        mTask = task;

        mTaskFuture = null;
    }

    @Override
    protected void startRunning() throws ServiceException {
        mTaskFuture = mExecutorService.submit(mTask);
    }

    @Override
    protected void stopRunning() {
        if (mTaskFuture != null) {
            mTaskFuture.cancel(true);
            mTaskFuture = null;
        }
    }
}
