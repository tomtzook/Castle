package com.castle.util.registration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Consumer;

public class BasicRegistry<T> implements Registry<T> {

    private final Collection<T> mRegistered;

    BasicRegistry(Collection<T> registered) {
        mRegistered = registered;
    }

    public BasicRegistry() {
        this(new ArrayList<>());
    }

    @Override
    public boolean register(T t) {
        return mRegistered.add(t);
    }

    @Override
    public void clear() {
        mRegistered.clear();
    }

    @Override
    public Collection<T> getRegistered(boolean clearRegistry) {
        if (clearRegistry) {
            Collection<T> copy = new ArrayList<>(mRegistered);
            mRegistered.clear();
            return copy;
        }

        return Collections.unmodifiableCollection(mRegistered);
    }

    @Override
    public void forEach(Consumer<? super T> consumer, boolean clearRegistry) {
        for (T t : mRegistered) {
            consumer.accept(t);
        }

        if (clearRegistry) {
            mRegistered.clear();
        }
    }
}
