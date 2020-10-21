package com.castle.concurrent;

import java.util.concurrent.Future;
import java.util.function.Consumer;

public interface FutureWatcher {

    void watch(Future<?> future, Consumer<Future<?>> whenDone);
}
