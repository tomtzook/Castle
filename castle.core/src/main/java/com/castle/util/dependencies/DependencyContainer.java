package com.castle.util.dependencies;

import java.util.Collection;
import java.util.Optional;

public interface DependencyContainer {

    static DependencyContainerImpl.Builder builder() {
        return new DependencyContainerImpl.Builder();
    }

    <T> T get(Class<T> type);
    <T> Optional<T> tryGet(Class<T> type);

    <T> Collection<T> getAllMatching(Class<T> type);

    void add(DependencySupplier supplier);
}
