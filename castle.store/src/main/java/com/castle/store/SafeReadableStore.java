package com.castle.store;

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface SafeReadableStore<T> extends ReadableStore<T> {

    @Override
    boolean exists(T value);
    @Override
    boolean existsAll(Collection<? extends T> values);

    @Override
    Optional<T> selectFirst(Predicate<? super T> filter);
    @Override
    Collection<T> selectAll(Predicate<? super T> filter);
    @Override
    Collection<T> selectAll();

    @Override
    void forEach(Consumer<? super T> consumer);
}
