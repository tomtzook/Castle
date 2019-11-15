package com.castle.repository;

import com.castle.repository.exceptions.KeyNotInRepositoryException;
import com.castle.util.concurrent.AtomicMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class ConcurrentInMemoryRepository<K, V> implements SafeRepository<K, V> {

    private final AtomicMap<K, V> mAtomicMap;

    ConcurrentInMemoryRepository(AtomicMap<K, V> map) {
        mAtomicMap = map;
    }

    public ConcurrentInMemoryRepository(Map<? extends K, ? extends V> values) {
        mAtomicMap = new AtomicMap<>(new HashMap<>(values));
    }

    @Override
    public boolean exists(K key) {
        return mAtomicMap.contains(key);
    }

    @Override
    public boolean exists(Collection<? extends K> keys) {
        return mAtomicMap.contains(keys);
    }

    @Override
    public V retrieve(K key) throws KeyNotInRepositoryException {
        V value = mAtomicMap.get(key);
        if (value == null) {
            throw new KeyNotInRepositoryException(String.valueOf(key));
        }

        return value;
    }

    @Override
    public Optional<V> tryRetrieve(K key) {
        V value = mAtomicMap.get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public Map<K, V> retrieveAll(Collection<? extends K> keys) {
        Map<K, V> copy = mAtomicMap.copy();
        Map<K, V> result = new HashMap<>(keys.size());

        for (K key : keys) {
            V value = copy.get(key);
            if (value != null) {
                result.put(key, value);
            }
        }

        return result;
    }

    @Override
    public Map<K, V> retrieveAll() {
        return mAtomicMap.copy();
    }

    @Override
    public Map<K, V> retrieveIf(BiPredicate<? super K, ? super V> predicate) {
        Map<K, V> copy = mAtomicMap.copy();

        return copy.entrySet().stream()
                .filter((entry) -> predicate.test(entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public void store(K key, V value) {
        store(Collections.singletonMap(key, value));
    }

    @Override
    public void store(Map<? extends K, ? extends V> values) {
        mAtomicMap.put(values);
    }

    @Override
    public V remove(K key) throws KeyNotInRepositoryException {
        V value = mAtomicMap.remove(key);
        if (value == null) {
            throw new KeyNotInRepositoryException(String.valueOf(key));
        }

        return value;
    }

    @Override
    public Optional<V> tryRemove(K key) {
        V value = mAtomicMap.remove(key);
        return Optional.ofNullable(value);
    }

    @Override
    public Map<K, V> removeAll(Collection<? extends K> keys) {
        return mAtomicMap.removeAll(keys);
    }

    @Override
    public void clear() {
        mAtomicMap.clear();
    }
}
