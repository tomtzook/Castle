package com.castle.store;

import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public interface WritableStore<T> {

    Set<Characteristic> characteristics();

    boolean add(T element) throws StoreException;
    boolean addAll(Collection<? extends T> collection) throws StoreException;

    boolean remove(T element) throws StoreException;
    boolean removeAll(Collection<T> collection) throws StoreException;
    boolean removeIf(Predicate<T> filter) throws StoreException;
    void clear() throws StoreException;
}
