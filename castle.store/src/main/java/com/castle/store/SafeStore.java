package com.castle.store;

import java.util.Collection;
import java.util.function.Consumer;

public interface SafeStore<T> extends Store<T>, SafeReadableStore<T>, SafeWritableStore<T> {

    @Override
    Collection<T> selectAll(boolean clear);

    @Override
    void forEach(Consumer<? super T> consumer, boolean clear);
}
