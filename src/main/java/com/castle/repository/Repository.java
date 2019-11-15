package com.castle.repository;

import com.castle.repository.exceptions.RepositoryException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiPredicate;

public interface Repository<K, V> {

    boolean exists(K key) throws RepositoryException;
    boolean exists(Collection<? extends K> keys) throws RepositoryException;

    V retrieve(K key) throws RepositoryException;
    Optional<V> tryRetrieve(K key) throws RepositoryException;
    Map<K, V> retrieveAll(Collection<? extends K> keys) throws RepositoryException;
    Map<K, V> retrieveAll() throws RepositoryException;

    Map<K, V> retrieveIf(BiPredicate<? super K, ? super V> predicate) throws RepositoryException;

    void store(K key, V value) throws RepositoryException;
    void store(Map<? extends K, ? extends V> values) throws RepositoryException;

    V remove(K key) throws RepositoryException;
    Optional<V> tryRemove(K key) throws RepositoryException;
    Map<K, V> removeAll(Collection<? extends K> keys) throws RepositoryException;
    void clear() throws RepositoryException;
}
