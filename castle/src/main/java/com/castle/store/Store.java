package com.castle.store;

import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface Store<K, V> {

    boolean exists(K key) throws StoreException;
    boolean exists(Collection<K> keys) throws StoreException;

    Optional<V> store(K key, V value) throws StoreException;
    void store(Map<K, V> values) throws StoreException;

    <T extends V> T retrieve(K key, Class<T> type) throws StoreException, KeyNotFoundException;
    <T extends V> Map<K, T> retrieve(Collection<K> keys, Class<T> type) throws StoreException, KeyNotFoundException;

    boolean delete(K key) throws StoreException;
    boolean delete(Collection<K> keys) throws StoreException;
}