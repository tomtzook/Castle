package com.castle.repository;

import com.castle.repository.exceptions.KeyNotInRepositoryException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class InMemoryRepository<K, V> implements SafeRepository<K, V> {

    private final Map<K, V> mMap;

    InMemoryRepository(Map<K, V> map) {
        mMap = map;
    }

    public static <K, V> InMemoryRepository<K, V> from(Map<? extends K, ? extends V> map) {
        return new InMemoryRepository<>(new HashMap<>(map));
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
    public Map<K, V> retrieveIf(BiPredicate<? super K, ? super V> predicate) {
        return mMap.entrySet().stream()
                .filter((entry) -> predicate.test(entry.getKey(), entry.getValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
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
