package com.castle.store;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

public interface SafeReadableKeyStore<K, V> extends ReadableKeyStore<K, V> {

    @Override
    boolean exists(K key);
    @Override
    boolean existsAll(Collection<? extends K> keys);

    @Override
    V retrieve(K key);
    @Override
    Optional<V> tryRetrieve(K key);
    @Override
    <T extends V> T retrieve(K key, Class<T> type);
    @Override
    <T extends V> Optional<T> tryRetrieve(K key, Class<T> type);

    @Override
    Map<K, V> retrieve(Collection<? extends K> keys);
    @Override
    <T extends V> Map<K, T> retrieve(Collection<? extends K> keys, Class<T> type);

    @Override
    Optional<V> retrieveFirst(BiPredicate<? super K, ? super V> filter);
    @Override
    Map<K, V> retrieveAll(BiPredicate<? super K, ? super V> filter);
    @Override
    Map<K, V> retrieveAll();

    @Override
    void forEach(BiConsumer<? super K, ? super V> consumer);
}
