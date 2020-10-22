package com.castle.concurrent.executor;

import com.castle.concurrent.service.TerminalServiceBase;
import com.castle.exceptions.ServiceException;
import com.castle.time.Time;
import com.castle.util.registration.ConcurrentRegistry;
import com.castle.util.registration.Registry;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public class MultiThreadPeriodicActionExecutor extends TerminalServiceBase implements ActionExecutor {

    private final ScheduledExecutorService mExecutorService;
    private final Supplier<Time> mRunInterval;
    private final Registry<Future<?>> mFutures;

    public MultiThreadPeriodicActionExecutor(ScheduledExecutorService executorService, Supplier<Time> runInterval) {
        mExecutorService = executorService;
        mRunInterval = runInterval;

        mFutures = new ConcurrentRegistry<>();
    }

    @Override
    public Future<?> execute(Action action) {
        Time runInterval = mRunInterval.get();
        Future<?> future = mExecutorService.scheduleAtFixedRate(new PeriodicActionContext(action),
                runInterval.value(), runInterval.value(), runInterval.unit());
        mFutures.register(future);
        return future;
    }

    @Override
    protected void startRunning() throws ServiceException {
    }

    @Override
    protected void stopRunning() {
        for (Future<?> future : mFutures.getRegistered(true)) {
            future.cancel(true);
        }
    }
}
