package com.castle.repository;

import com.castle.repository.exceptions.KeyNotInRepositoryException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class InMemoryRepository<K, V> implements SafeRepository<K, V> {

    private final Map<K, V> mMap;

    public InMemoryRepository(Map<? extends K, ? extends V> map) {
        mMap = new HashMap<>(map);
    }

    @Override
    public boolean exists(K key) {
        return mMap.containsKey(key);
    }

    @Override
    public boolean exists(Collection<? extends K> keys) {
        return mMap.keySet().containsAll(keys);
    }

    @Override
    public V retrieve(K key) throws KeyNotInRepositoryException {
        V value = mMap.get(key);
        if (value == null) {
            throw new KeyNotInRepositoryException(String.valueOf(key));
        }

        return value;
    }

    @Override
    public Optional<V> tryRetrieve(K key) {
        V value = mMap.get(key);
        return Optional.ofNullable(value);
    }

    @Override
    public Map<K, V> retrieveAll(Collection<? extends K> keys) {
        Map<K, V> result = new HashMap<>(keys.size());

        for (K key : keys) {
            V value = mMap.get(key);
            if (value != null) {
                result.put(key, value);
            }
        }

        return result;
    }

    @Override
    public Map<K, V> retrieveAll() {
        return new HashMap<>(mMap);
    }

    @Override
    public void store(K key, V value) {
        mMap.put(key, value);
    }

    @Override
    public void store(Map<? extends K, ? extends V> values) {
        mMap.putAll(values);
    }

    @Override
    public V remove(K key) throws KeyNotInRepositoryException {
        V value = mMap.remove(key);
        if (value == null) {
            throw new KeyNotInRepositoryException(String.valueOf(key));
        }

        return value;
    }

    @Override
    public Optional<V> tryRemove(K key) {
        V value = mMap.remove(key);
        return Optional.ofNullable(value);
    }

    @Override
    public Map<K, V> removeAll(Collection<? extends K> keys) {
        Map<K, V> removed = new HashMap<>(keys.size());

        for (K key : keys) {
            V value = mMap.remove(key);
            if (value != null) {
                removed.put(key, value);
            }
        }

        return removed;
    }

    @Override
    public void clear() {
        mMap.clear();
    }
}
