package com.castle.store;

import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.function.Consumer;

public interface Store<T> extends ReadableStore<T>, WritableStore<T> {

    Collection<T> getAll(boolean clear) throws StoreException;
    void forEach(Consumer<? super T> consumer, boolean clear) throws StoreException;
}
