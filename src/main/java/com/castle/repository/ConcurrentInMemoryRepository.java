package com.castle.repository;

import com.castle.repository.exceptions.KeyNotInRepositoryException;
import com.castle.util.concurrent.AtomicMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ConcurrentInMemoryRepository<K, V> implements SafeRepository<K, V> {

    private final AtomicMap<K, V> mAtomicMap;

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
        Map<K, V> result = new HashMap<>();

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
