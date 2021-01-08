package com.castle.store;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SafeWwritableKeyStore<K, V> extends WritableKeyStore<K, V> {

    @Override
    Optional<V> store(K key, V value);
    @Override
    void storeAll(Map<? extends K, ? extends V> values);

    @Override
    boolean delete(K key);
    @Override
    boolean deleteAll(Collection<? extends K> keys);
}
