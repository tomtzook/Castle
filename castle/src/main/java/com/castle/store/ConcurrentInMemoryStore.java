package com.castle.store;

import com.castle.annotations.ThreadSafe;
import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@ThreadSafe
public class ConcurrentInMemoryStore<K, V> implements Store<K, V> {

    private final ConcurrentMap<K, V> mMap;
    private final Map<K, V> mDefaultValues;

    ConcurrentInMemoryStore(ConcurrentMap<K, V> map, Map<K, V> defaultValues) {
        mMap = map;
        mDefaultValues = defaultValues;
    }

    public ConcurrentInMemoryStore(Map<K, V> defaultValues) {
        this(new ConcurrentHashMap<>(), defaultValues);
    }

    @Override
    public boolean exists(K key) throws StoreException {
        return mDefaultValues.containsKey(key) || mMap.containsKey(key);
    }

    @Override
    public boolean exists(Collection<? extends K> keys) throws StoreException {
        Map<K, V> mapSnapshot = new HashMap<>(mDefaultValues);
        mapSnapshot.putAll(mMap);

        for (K key : keys) {
            if (!mapSnapshot.containsKey(key)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public Optional<V> store(K key, V value) throws StoreException {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");

        V defaultValue = mDefaultValues.get(key);
        V oldValue = mMap.put(key, value);
        if (oldValue == null) {
            oldValue = defaultValue;
        }

        return Optional.ofNullable(oldValue);
    }

    @Override
    public void store(Map<? extends K, ? extends V> values) throws StoreException {
        Objects.requireNonNull(values, "values");
        mMap.putAll(values);
    }

    @Override
    public <T extends V> T retrieve(K key, Class<T> type) throws StoreException, KeyNotFoundException {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(type, "type");

        V defaultValue = mDefaultValues.get(key);

        V value = mMap.getOrDefault(key, defaultValue);
        if (value == null) {
            throw new KeyNotFoundException(String.valueOf(key));
        }

        return type.cast(value);
    }

    @Override
    public <T extends V> Map<K, T> retrieve(Collection<? extends K> keys, Class<T> type) throws StoreException {
        Objects.requireNonNull(keys, "keys");
        Objects.requireNonNull(type, "type");

        Map<K, V> mapSnapshot = new HashMap<>(mDefaultValues);
        mapSnapshot.putAll(mMap);

        Map<K, T> result = new HashMap<>();
        for (K key : keys) {
            V value = mapSnapshot.get(key);

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
