package com.castle.supervisor;

import com.castle.concurrent.service.Service;

import java.util.Collection;

public interface Supervisor {

    void register(AutoCloseable closeable);
    void register(AutoCloseable... closeables);
    void register(Collection<? extends AutoCloseable> closeables);

    void register(Service service);

    ThreadState registerThread(Runnable runnable, ThreadOption... options);
}
