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
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

@ThreadSafe
public class ThreadSafeInMemoryKeyStore<K, V> implements SafeKeyStore<K, V> {

    private final ConcurrentMap<K, V> mMap;
    private final Map<K, V> mDefaultValues;

    ThreadSafeInMemoryKeyStore(ConcurrentMap<K, V> map, Map<K, V> defaultValues) {
        mMap = map;
        mDefaultValues = defaultValues;
    }

    public ThreadSafeInMemoryKeyStore(Map<K, V> defaultValues) {
        this(new ConcurrentHashMap<>(), defaultValues);
    }

    @Override
    public boolean exists(K key) {
        return mDefaultValues.containsKey(key) || mMap.containsKey(key);
    }

    @Override
    public boolean existsAll(Collection<? extends K> keys) {
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
    public Optional<V> store(K key, V value) {
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
    public void storeAll(Map<? extends K, ? extends V> values) {
        Objects.requireNonNull(values, "values");
        mMap.putAll(values);
    }

    @Override
    public V retrieve(K key) {
        Objects.requireNonNull(key, "key");

        V defaultValue = mDefaultValues.get(key);

        V value = mMap.getOrDefault(key, defaultValue);
        if (value == null) {
            throw new KeyNotFoundException(String.valueOf(key));
        }

        return value;
    }

    @Override
    public Optional<V> tryRetrieve(K key) {
        Objects.requireNonNull(key, "key");

        V defaultValue = mDefaultValues.get(key);

        V value = mMap.getOrDefault(key, defaultValue);
        return Optional.ofNullable(value);
    }

    @Override
    public <T extends V> T retrieve(K key, Class<T> type) {
        Objects.requireNonNull(type, "type");

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
        Objects.requireNonNull(keys, "keys");

        Map<K, V> mapSnapshot = new HashMap<>(mDefaultValues);
        mapSnapshot.putAll(mMap);

        Map<K, V> result = new HashMap<>();
        for (K key : keys) {
            V value = mapSnapshot.get(key);

            if (value != null) {
                result.put(key, value);
            }
        }

        return result;
    }

    @Override
    public <T extends V> Map<K, T> retrieve(Collection<? extends K> keys, Class<T> type) {
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
    public Optional<V> retrieveFirst(BiPredicate<? super K, ? super V> filter) {
        for (Map.Entry<K, V> entry : mMap.entrySet()) {
            if (filter.test(entry.getKey(), entry.getValue())) {
                return Optional.of(entry.getValue());
            }
        }

        return Optional.empty();
    }

    @Override
    public Map<K, V> retrieveAll(BiPredicate<? super K, ? super V> filter) {
        Map<K, V> result = new HashMap<>();
        for (Map.Entry<K, V> entry : mMap.entrySet()) {
            if (filter.test(entry.getKey(), entry.getValue())) {
                result.put(entry.getKey(), entry.getValue());
            }
        }

        return result;
    }

    @Override
    public Map<K, V> retrieveAll() {
        return new HashMap<>(mMap);
    }

    @Override
    public void forEach(BiConsumer<? super K, ? super V> consumer) {
        mMap.forEach(consumer);
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
