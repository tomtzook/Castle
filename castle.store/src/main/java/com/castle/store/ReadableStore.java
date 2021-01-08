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
    boolean existsAll(Collection<? extends T> values) throws StoreException;

    Optional<T> selectFirst(Predicate<? super T> filter) throws StoreException;
    Collection<T> selectAll(Predicate<? super T> filter) throws StoreException;
    Collection<T> selectAll() throws StoreException;

    void forEach(Consumer<? super T> consumer) throws StoreException;
}
