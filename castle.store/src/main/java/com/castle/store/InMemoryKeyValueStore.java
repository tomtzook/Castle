package com.castle.store;

import com.castle.annotations.NotThreadSafe;
import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NotThreadSafe
public class InMemoryKeyValueStore<K, V> implements KeyValueStore<K, V> {

    private final Map<K, V> mMap;
    private final Map<K, V> mDefaultValues;

    InMemoryKeyValueStore(Map<K, V> map, Map<K, V> defaultValues) {
        mMap = map;
        mDefaultValues = defaultValues;
    }

    public InMemoryKeyValueStore(Map<K, V> defaultValues) {
        this(new HashMap<>(), defaultValues);
    }

    @Override
    public boolean exists(K key) throws StoreException {
        return mDefaultValues.containsKey(key) || mMap.containsKey(key);
    }

    @Override
    public boolean exists(Collection<? extends K> keys) throws StoreException {
        return mDefaultValues.keySet().containsAll(keys) || mMap.keySet().containsAll(keys);
    }

    @Override
    public Optional<V> store(K key, V value) throws StoreException {
        V oldValue = mMap.put(key, value);
        if (oldValue == null) {
            oldValue = mDefaultValues.get(key);
        }

        return Optional.ofNullable(oldValue);
    }

    @Override
    public void store(Map<? extends K, ? extends V> values) throws StoreException {
        mMap.putAll(values);
    }

    @Override
    public <T extends V> T retrieve(K key, Class<T> type) throws StoreException, KeyNotFoundException {
        V defaultValue = mDefaultValues.get(key);

        V value = mMap.getOrDefault(key, defaultValue);
        if (value == null) {
            throw new KeyNotFoundException(String.valueOf(key));
        }

        return type.cast(value);
    }

    @Override
    public <T extends V> Map<K, T> retrieve(Collection<? extends K> keys, Class<T> type) throws StoreException {
        Map<K, T> result = new HashMap<>();
        for (K key : keys) {
            V defaultValue = mDefaultValues.get(key);
            V value = mMap.getOrDefault(key, defaultValue);

            if (value != null) {
                result.put(key, type.cast(value));
            }
        }

        return result;
    }

    @Override
    public boolean delete(K key) throws StoreException {
        return mMap.remove(key) != null;
    }

    @Override
    public boolean delete(Collection<? extends K> keys) throws StoreException {
        return mMap.keySet().removeAll(keys);
    }
}
