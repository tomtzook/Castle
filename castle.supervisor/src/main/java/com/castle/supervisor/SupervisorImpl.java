package com.castle.supervisor;

import com.castle.concurrent.service.Service;
import com.castle.concurrent.service.TerminalService;
import com.castle.store.Characteristic;
import com.castle.store.SafeStore;
import com.castle.store.Stores;
import com.castle.util.closeables.Closer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadFactory;

public class SupervisorImpl implements SupervisorControl {

    private final ThreadFactory mThreadFactory;
    private final SafeStore<Service> mServiceStore;
    private final SafeStore<MonitoredThread> mThreadStore;
    private final Closer mResources;

    private Thread mWatchdogThread;

    public SupervisorImpl(ThreadFactory threadFactory) {
        mThreadFactory = threadFactory;
        mServiceStore = Stores.newSafeStore(Characteristic.NO_DUPLICATIONS, Characteristic.THREAD_SAFE);
        mThreadStore = Stores.newSafeStore(Characteristic.NO_DUPLICATIONS, Characteristic.THREAD_SAFE);
        mResources = Closer.empty();
    }

    public SupervisorImpl() {
        this(new DefaultThreadFactory());
    }

    @Override
    public void register(AutoCloseable closeable) {
        mResources.add(closeable);
    }

    @Override
    public void register(AutoCloseable... closeables) {
        mResources.addAll(closeables);
    }

    @Override
    public void register(Collection<? extends AutoCloseable> closeables) {
        mResources.addAll(closeables);
    }

    @Override
    public void register(Service service) {
        mServiceStore.insert(service);
    }

    @Override
    public ThreadState registerThread(Runnable runnable, ThreadOption... options) {
        Set<ThreadOption> optionSet = new HashSet<>(Arrays.asList(options));
        MonitoredThread monitoredThread = new MonitoredThreadImpl(mThreadFactory, runnable, optionSet);
        mThreadStore.insert(monitoredThread);

        return monitoredThread;
    }

    @Override
    public void onStart() {
        for (Service service : mServiceStore.selectAll()) {
            try {
                if (!service.isRunning()) {
                    service.start();
                }
            } catch (Throwable t) { }
        }

        for (MonitoredThread thread : mThreadStore.selectAll()) {
            try {
                if (!thread.isRunning()) {
                    thread.start();
                }
            } catch (Throwable t) { }
        }

        mWatchdogThread = new Thread(new WatchdogTask(mServiceStore, mThreadStore));
        mWatchdogThread.setDaemon(true);
        mWatchdogThread.setName("supervisor-watchdog");
        mWatchdogThread.start();
    }

    @Override
    public void onShutdown() {
        if (mWatchdogThread != null) {
            mWatchdogThread.interrupt();

            try {
                mWatchdogThread.join(500);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            mWatchdogThread = null;
        }

        for (Service service : mServiceStore.selectAll(true)) {
            try {
                if (service.isRunning()) {
                    service.stop();
                }

                if (service instanceof TerminalService) {
                    TerminalService terminalService = (TerminalService) service;
                    if (!terminalService.isClosed()) {
                        terminalService.close();
                    }
                }
            } catch (Throwable t) { }
        }

        for (MonitoredThread thread : mThreadStore.selectAll(true)) {
            try {
                if (thread.isRunning()) {
                    thread.stop();
                }
            } catch (Throwable t) { }
        }

        try {
            mResources.close();
        } catch (Throwable t) { }
    }

    private static class DefaultThreadFactory implements ThreadFactory {

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setDaemon(true);

            return thread;
        }
    }

    private static class WatchdogTask implements Runnable {

        private final SafeStore<Service> mServiceStore;
        private final SafeStore<MonitoredThread> mThreadStore;

        private WatchdogTask(SafeStore<Service> serviceStore, SafeStore<MonitoredThread> threadStore) {
            mServiceStore = serviceStore;
            mThreadStore = threadStore;
        }

        @Override
        public void run() {
            for (Service service : mServiceStore.selectAll()) {
                try {
                    if (!service.isRunning()) {
                        service.start();
                    }
                } catch (Throwable t) { }
            }

            for (MonitoredThread thread : mThreadStore.selectAll()) {
                try {
                    if (!thread.isRunning()) {
                        if (thread.options().contains(StandardThreadOption.KEEP_ALIVE)) {
                            thread.start();
                        } else {
                            mThreadStore.delete(thread);
                        }
                    }
                } catch (Throwable t) { }
            }
        }
    }
}
