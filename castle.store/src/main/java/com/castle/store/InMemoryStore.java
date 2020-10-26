package com.castle.store;

import com.castle.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@NotThreadSafe
public class InMemoryStore<T> implements Store<T> {

    private final Collection<T> mRegistered;
    private final Set<Characteristic> mCharacteristics;

    InMemoryStore(Collection<T> registered, Set<Characteristic> characteristics) {
        mRegistered = registered;
        mCharacteristics = Collections.unmodifiableSet(characteristics);
    }

    @Override
    public Set<Characteristic> characteristics() {
        return mCharacteristics;
    }

    @Override
    public boolean add(T t) {
        return mRegistered.add(t);
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        return mRegistered.addAll(collection);
    }

    @Override
    public boolean remove(T t) {
        return mRegistered.remove(t);
    }

    @Override
    public boolean removeAll(Collection<T> collection) {
        return mRegistered.removeAll(collection);
    }

    @Override
    public boolean removeIf(Predicate<T> filter) {
        return mRegistered.removeIf(filter);
    }

    @Override
    public void clear() {
        mRegistered.clear();
    }

    @Override
    public boolean exists(T value) {
        return mRegistered.contains(value);
    }

    @Override
    public boolean exists(Collection<? extends T> values) {
        return mRegistered.containsAll(values);
    }

    @Override
    public Collection<T> getAll() {
        return Collections.unmodifiableCollection(mRegistered);
    }

    @Override
    public Collection<T> getAll(Predicate<? super T> filter) {
        Collection<T> all = new ArrayList<>();
        for(T element : mRegistered) {
            if (filter.test(element)) {
                all.add(element);
            }
        }

        return all;
    }

    @Override
    public Optional<T> getFirst(Predicate<? super T> filter) {
        for(T element : mRegistered) {
            if (filter.test(element)) {
                return Optional.of(element);
            }
        }

        return Optional.empty();
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {
        for (T t : mRegistered) {
            consumer.accept(t);
        }
    }

    @Override
    public Collection<T> getAll(boolean clear) {
        if (clear) {
            Collection<T> copy = new ArrayList<>(mRegistered);
            mRegistered.clear();
            return copy;
        }

        return getAll();
    }

    @Override
    public void forEach(Consumer<? super T> consumer, boolean clear) {
        forEach(consumer);

        if (clear) {
            mRegistered.clear();
        }
    }
}
