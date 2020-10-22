package com.castle.concurrent.executor;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.castle.util.registration.ConcurrentRegistry;
import com.castle.util.registration.Registry;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MultipleThreadActionExecutor extends TerminalServiceBase implements ActionExecutor {

    private final ExecutorService mExecutorService;
    private final Registry<Future<?>> mFutures;

    public MultipleThreadActionExecutor(ExecutorService executorService) {
        mExecutorService = executorService;
        mFutures = new ConcurrentRegistry<>();
    }

    @Override
    public Future<?> execute(Action action) {
        Future<?> future = mExecutorService.submit(new RunnableActionContext(action));
        mFutures.register(future);
        return future;
    }

    @Override
    protected void startRunning() throws ServiceException {
    }

    @Override
    protected void stopRunning() {
        mFutures.forEach((future)-> future.cancel(true), true);
    }
}
