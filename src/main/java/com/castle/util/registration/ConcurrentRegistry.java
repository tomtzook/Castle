package com.castle.util.registration;

import java.util.ArrayList;
import java.util.Collection;

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
}
