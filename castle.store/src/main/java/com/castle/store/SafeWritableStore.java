package com.castle.store;

import java.util.Collection;
import java.util.function.Predicate;

public interface SafeWritableStore<T> extends WritableStore<T> {

    @Override
    boolean insert(T element);
    @Override
    boolean insertAll(Collection<? extends T> collection);

    @Override
    boolean delete(T element);
    @Override
    boolean deleteFirst(Predicate<T> filter);
    @Override
    boolean deleteAll(Predicate<T> filter);
    @Override
    boolean deleteAll(Collection<T> collection);

    @Override
    void clear();
}
