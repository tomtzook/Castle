package com.castle.store;

import com.castle.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

@NotThreadSafe
public class InMemoryStore<T> implements Store<T> {

    private final Collection<T> mElements;
    private final Set<Characteristic> mCharacteristics;

    InMemoryStore(Collection<T> elements, Set<Characteristic> characteristics) {
        mElements = elements;
        mCharacteristics = Collections.unmodifiableSet(characteristics);
    }

    @Override
    public Set<Characteristic> characteristics() {
        return mCharacteristics;
    }

    @Override
    public boolean insert(T t) {
        return mElements.add(t);
    }

    @Override
    public boolean insertAll(Collection<? extends T> collection) {
        return mElements.addAll(collection);
    }

    @Override
    public boolean delete(T t) {
        return mElements.remove(t);
    }

    @Override
    public boolean deleteFirst(Predicate<T> filter) {
        boolean removed = false;

        Iterator<T> iterator = mElements.iterator();
        while (iterator.hasNext()) {
            if (filter.test(iterator.next())) {
                iterator.remove();
                removed = true;
                break;
            }
        }

        return removed;
    }

    @Override
    public boolean deleteAll(Predicate<T> filter) {
        return mElements.removeIf(filter);
    }

    @Override
    public boolean deleteAll(Collection<T> collection) {
        return mElements.removeAll(collection);
    }

    @Override
    public void clear() {
        mElements.clear();
    }

    @Override
    public boolean exists(T value) {
        return mElements.contains(value);
    }

    @Override
    public boolean exists(Collection<? extends T> values) {
        return mElements.containsAll(values);
    }

    @Override
    public Optional<T> selectFirst(Predicate<? super T> filter) {
        for(T element : mElements) {
            if (filter.test(element)) {
                return Optional.of(element);
            }
        }

        return Optional.empty();
    }

    @Override
    public Collection<T> selectAll(Predicate<? super T> filter) {
        Collection<T> all = new ArrayList<>();
        for(T element : mElements) {
            if (filter.test(element)) {
                all.add(element);
            }
        }

        return all;
    }

    @Override
    public Collection<T> selectAll() {
        return Collections.unmodifiableCollection(mElements);
    }

    @Override
    public void forEach(Consumer<? super T> consumer) {
        for (T t : mElements) {
            consumer.accept(t);
        }
    }

    @Override
    public Collection<T> getAll(boolean clear) {
        if (clear) {
            Collection<T> copy = new ArrayList<>(mElements);
            mElements.clear();
            return copy;
        }

        return selectAll();
    }

    @Override
    public void forEach(Consumer<? super T> consumer, boolean clear) {
        forEach(consumer);

        if (clear) {
            mElements.clear();
        }
    }
}
