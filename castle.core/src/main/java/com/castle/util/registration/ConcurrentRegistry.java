package com.castle.util.registration;

import com.castle.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

@ThreadSafe
public class ConcurrentRegistry<T> implements Registry<T> {

    private final Collection<T> mRegistered;

    public ConcurrentRegistry() {
        mRegistered = new ArrayList<>();
    }

    @Override
    public boolean register(T element) {
        synchronized (mRegistered) {
            return mRegistered.add(element);
        }
    }

    @Override
    public void clear() {
        synchronized (mRegistered) {
            mRegistered.clear();
        }
    }

    @Override
    public Collection<T> getRegistered(boolean clearRegistry) {
        Collection<T> copy;
        synchronized (mRegistered) {
            copy = new ArrayList<>(mRegistered);
            if (clearRegistry) {
                mRegistered.clear();
            }
        }

        return copy;
    }

    @Override
    public void forEach(Consumer<? super T> consumer, boolean clearRegistry) {
        Collection<T> copy = getRegistered(clearRegistry);
        for (T t : copy) {
            consumer.accept(t);
        }
    }
}
