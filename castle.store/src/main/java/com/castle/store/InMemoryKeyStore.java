package com.castle.store;

import com.castle.annotations.NotThreadSafe;
import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@NotThreadSafe
public class InMemoryKeyStore<K, V> implements SafeKeyStore<K, V> {

    private final Map<K, V> mMap;
    private final Map<K, V> mDefaultValues;

    InMemoryKeyStore(Map<K, V> map, Map<K, V> defaultValues) {
        mMap = map;
        mDefaultValues = defaultValues;
    }

    public InMemoryKeyStore(Map<K, V> defaultValues) {
        this(new HashMap<>(), defaultValues);
    }

    @Override
    public boolean exists(K key) {
        return mDefaultValues.containsKey(key) || mMap.containsKey(key);
    }

    @Override
    public boolean existsAll(Collection<? extends K> keys) {
        return mDefaultValues.keySet().containsAll(keys) || mMap.keySet().containsAll(keys);
    }

    @Override
    public Optional<V> store(K key, V value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");

        V oldValue = mMap.put(key, value);
        if (oldValue == null) {
            oldValue = mDefaultValues.get(key);
        }

        return Optional.ofNullable(oldValue);
    }

    @Override
    public void storeAll(Map<? extends K, ? extends V> values) {
        mMap.putAll(values);
    }

    @Override
    public V retrieve(K key) {
        V defaultValue = mDefaultValues.get(key);

        V value = mMap.getOrDefault(key, defaultValue);
        if (value == null) {
            throw new KeyNotFoundException(String.valueOf(key));
        }

        return value;
    }

    @Override
    public Optional<V> tryRetrieve(K key) {
        V defaultValue = mDefaultValues.get(key);

        V value = mMap.getOrDefault(key, defaultValue);
        return Optional.ofNullable(value);
    }

    @Override
    public <T extends V> T retrieve(K key, Class<T> type) {
        V value = retrieve(key);
        return type.cast(value);
    }

    @Override
    public <T extends V> Optional<T> tryRetrieve(K key, Class<T> type) {
        Optional<V> optional = tryRetrieve(key);
        return optional.map(type::cast);
    }

    @Override
    public Map<K, V> retrieve(Collection<? extends K> keys) {
        Map<K, V> result = new HashMap<>();
        for (K key : keys) {
            V defaultValue = mDefaultValues.get(key);
            V value = mMap.getOrDefault(key, defaultValue);

            if (value != null) {
                result.put(key, value);
            }
        }

        return result;
    }

    @Override
    public <T extends V> Map<K, T> retrieve(Collection<? extends K> keys, Class<T> type) {
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
    public boolean delete(K key) {
        return mMap.remove(key) != null;
    }

    @Override
    public boolean deleteAll(Collection<? extends K> keys) {
        return mMap.keySet().removeAll(keys);
    }
}
