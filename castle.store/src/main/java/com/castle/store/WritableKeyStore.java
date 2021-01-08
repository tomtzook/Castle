package com.castle.store;

import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface WritableKeyStore<K, V> {

    Optional<V> store(K key, V value) throws StoreException;
    void storeAll(Map<? extends K, ? extends V> values) throws StoreException;

    boolean delete(K key) throws StoreException;
    boolean deleteAll(Collection<? extends K> keys) throws StoreException;
}
