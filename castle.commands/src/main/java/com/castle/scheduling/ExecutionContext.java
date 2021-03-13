package com.castle.scheduling;

import com.castle.util.dependencies.DependencyContainer;

public interface ExecutionContext {

    boolean execute(DependencyContainer dependencyContainer);
}
