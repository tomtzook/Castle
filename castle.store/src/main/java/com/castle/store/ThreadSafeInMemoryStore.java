package com.castle.store;

import com.castle.annotations.ThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;

@ThreadSafe
public class ThreadSafeInMemoryStore<T> implements Store<T> {

    private final Collection<T> mElements;
    private final Set<Characteristic> mCharacteristics;
    private final ReadWriteLock mLock;

    ThreadSafeInMemoryStore(Collection<T> elements, Set<Characteristic> characteristics) {
        mElements = elements;
        mCharacteristics = characteristics;
        mLock = new ReentrantReadWriteLock();
    }

    @Override
    public Set<Characteristic> characteristics() {
        return mCharacteristics;
    }

    @Override
    public boolean add(T element) {
        mLock.writeLock().lock();
        try {
            return mElements.add(element);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public boolean addAll(Collection<? extends T> collection) {
        mLock.writeLock().lock();
        try {
            return mElements.addAll(collection);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public boolean remove(T element) {
        mLock.writeLock().lock();
        try {
            return mElements.remove(element);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeAll(Collection<T> collection) {
        mLock.writeLock().lock();
        try {
            return mElements.removeAll(collection);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public boolean removeIf(Predicate<T> filter) {
        mLock.writeLock().lock();
        try {
            return mElements.removeIf(filter);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public void clear() {
        mLock.writeLock().lock();
        try {
            mElements.clear();
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public boolean exists(T value) {
        mLock.readLock().lock();
        try {
            return mElements.contains(value);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public boolean exists(Collection<? extends T> values) {
        mLock.readLock().lock();
        try {
            return mElements.containsAll(values);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public Collection<T> getAll() {
        mLock.readLock().lock();
        try {
            return new ArrayList<>(mElements);
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public Collection<T> getAll(Predicate<? super T> filter) {
        mLock.readLock().lock();
        try {
            Collection<T> all = new ArrayList<>();
            for (T element : mElements) {
                if (filter.test(element)) {
                    all.add(element);
                }
            }

            return all;
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public Optional<T> getFirst(Predicate<? super T> filter) {
        mLock.readLock().lock();
        try {
            for (T element : mElements) {
                if (filter.test(element)) {
                    return Optional.of(element);
                }
            }

            return Optional.empty();
        } finally {
            mLock.readLock().unlock();
        }
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {
        Collection<T> copy = getAll();
        for (T t : copy) {
            consumer.accept(t);
        }
    }

    @Override
    public Collection<T> getAll(boolean clear) {
        if (!clear) {
            return getAll();
        }

        mLock.writeLock().lock();
        try {
            Collection<T> copy = new ArrayList<>(mElements);
            mElements.clear();
            return copy;
        } finally {
            mLock.writeLock().unlock();
        }
    }

    @Override
    public void forEach(Consumer<? super T> consumer, boolean clear) {
        Collection<T> copy = getAll(clear);
        for (T t : copy) {
            consumer.accept(t);
        }
    }
}
