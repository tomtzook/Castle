package com.castle.concurrent.executor;

import java.util.concurrent.Future;

public interface ActionExecutor {

    Future<?> execute(Action action);
}
