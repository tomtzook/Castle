package com.castle.store;

import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public interface ReadableStore<T> {

    Set<Characteristic> characteristics();

    boolean exists(T value) throws StoreException;
    boolean exists(Collection<? extends T> values) throws StoreException;

    Collection<T> getAll() throws StoreException;
    Collection<T> getAll(Predicate<? super T> filter) throws StoreException;
    Optional<T> getFirst(Predicate<? super T> filter) throws StoreException;

    void forEach(Consumer<? super T> consumer) throws StoreException;
}
