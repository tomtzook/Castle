package com.castle.store;

import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.Set;
import java.util.function.Predicate;

public interface WritableStore<T> {

    Set<Characteristic> characteristics();

    boolean insert(T element) throws StoreException;
    boolean insertAll(Collection<? extends T> collection) throws StoreException;

    boolean delete(T element) throws StoreException;
    boolean deleteFirst(Predicate<T> filter) throws StoreException;
    boolean deleteAll(Predicate<T> filter) throws StoreException;
    boolean deleteAll(Collection<T> collection) throws StoreException;

    void clear() throws StoreException;
}
