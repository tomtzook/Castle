package com.castle.repository;

import com.castle.repository.exceptions.KeyNotInRepositoryException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface SafeRepository<K, V> extends Repository<K, V> {

    @Override
    boolean exists(K key);
    @Override
    boolean exists(Collection<? extends K> keys);

    @Override
    V retrieve(K key) throws KeyNotInRepositoryException;
    @Override
    Optional<V> tryRetrieve(K key);
    @Override
    Map<K, V> retrieveAll(Collection<? extends K> keys);
    @Override
    Map<K, V> retrieveAll();

    @Override
    void store(K key, V value);
    @Override
    void store(Map<? extends K, ? extends V> values);

    @Override
    V remove(K key) throws KeyNotInRepositoryException;
    @Override
    Optional<V> tryRemove(K key);
    @Override
    Map<K, V> removeAll(Collection<? extends K> keys);
    @Override
    void clear();
}
