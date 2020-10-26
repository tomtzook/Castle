package com.castle.store;

import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface ReadableKeyStore<K, V> {

    boolean exists(K key) throws StoreException;
    boolean existsAll(Collection<? extends K> keys) throws StoreException;

    V retrieve(K key) throws StoreException;
    Optional<V> tryRetrieve(K key) throws StoreException;
    <T extends V> T retrieve(K key, Class<T> type) throws StoreException;
    <T extends V> Optional<T> tryRetrieve(K key, Class<T> type) throws StoreException;

    Map<K, V> retrieve(Collection<? extends K> keys) throws StoreException;
    <T extends V> Map<K, T> retrieve(Collection<? extends K> keys, Class<T> type) throws StoreException;
}
