package com.castle.util.registration;

import java.util.Collection;
import java.util.function.Consumer;

public interface Registry<T> {

    boolean register(T t);
    void clear();

    Collection<T> getRegistered(boolean clearRegistry);

    void forEach(Consumer<? super T> consumer, boolean clearRegistry);
}
