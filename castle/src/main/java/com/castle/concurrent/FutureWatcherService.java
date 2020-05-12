package com.castle.concurrent;

import com.castle.concurrent.service.PeriodicTaskService;
import com.castle.time.Time;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FutureWatcherService extends PeriodicTaskService implements FutureWatcher {

    private final ConcurrentMap<Future<?>, Consumer<Future<?>>> mMap;

    FutureWatcherService(ScheduledExecutorService executorService, Runnable task, Supplier<Time> periodSupplier,
                         ConcurrentMap<Future<?>, Consumer<Future<?>>> map) {
        super(executorService, task, periodSupplier);
        mMap = map;
    }

    FutureWatcherService(ScheduledExecutorService executorService, Supplier<Time> periodSupplier,
                         ConcurrentMap<Future<?>, Consumer<Future<?>>> map) {
        this(executorService, new Task(map), periodSupplier, map);
    }

    public FutureWatcherService(ScheduledExecutorService executorService, Supplier<Time> periodSupplier) {
        this(executorService, periodSupplier, new ConcurrentHashMap<>());
    }

    @Override
    public void watch(Future<?> future, Consumer<Future<?>> whenDone) {
        mMap.put(future, whenDone);
    }

    private static class Task implements Runnable {

        private final ConcurrentMap<Future<?>, Consumer<Future<?>>> mMap;

        private Task(ConcurrentMap<Future<?>, Consumer<Future<?>>> map) {
            mMap = map;
        }

        @Override
        public void run() {
            Collection<Future<?>> toRemove = new ArrayList<>();
            for (Map.Entry<Future<?>, Consumer<Future<?>>> entry : mMap.entrySet()) {
                Future<?> future = entry.getKey();
                if (future.isDone() || future.isCancelled()) {
                    toRemove.add(future);
                    entry.getValue().accept(future);
                }
            }

            toRemove.forEach(mMap::remove);
        }
    }
}
