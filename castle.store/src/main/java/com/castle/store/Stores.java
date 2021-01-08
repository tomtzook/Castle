package com.castle.store;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

public final class Stores {

    private Stores() {}

    public static <T> Store<T> newStore(Set<Characteristic> characteristics) {
        Collection<T> collection = collectionByCharacteristics(characteristics);

        if (characteristics.contains(Characteristic.THREAD_SAFE)) {
            return new ThreadSafeInMemoryStore<>(collection, characteristics);
        } else {
            return new InMemoryStore<>(collection, characteristics);
        }
    }

    public static <T> Store<T> newStore(Characteristic... characteristics) {
        Set<Characteristic> characteristicSet = new HashSet<>();
        Collections.addAll(characteristicSet, characteristics);
        return newStore(characteristicSet);
    }

    public static <T> Store<T> newStore() {
        return newStore(Collections.emptySet());
    }

    public static <T> SafeStore<T> newSafeStore(Set<Characteristic> characteristics) {
        Collection<T> collection = collectionByCharacteristics(characteristics);

        if (characteristics.contains(Characteristic.THREAD_SAFE)) {
            return new ThreadSafeInMemoryStore<>(collection, characteristics);
        } else {
            return new InMemoryStore<>(collection, characteristics);
        }
    }

    public static <T> SafeStore<T> newSafeStore(Characteristic... characteristics) {
        Set<Characteristic> characteristicSet = new HashSet<>();
        Collections.addAll(characteristicSet, characteristics);
        return newSafeStore(characteristicSet);
    }

    public static <T> SafeStore<T> newSafeStore() {
        return newSafeStore(Collections.emptySet());
    }

    public static <K, V> KeyStore<K, V> newKeyStore(Set<Characteristic> characteristics) {
        if (characteristics.contains(Characteristic.THREAD_SAFE)) {
            ConcurrentMap<K, V> map = concurrentMapByCharacteristics(characteristics);
            return new ThreadSafeInMemoryKeyStore<>(map);
        } else {
            Map<K, V> map = mapByCharacteristics(characteristics);
            return new InMemoryKeyStore<>(map);
        }
    }

    public static <K, V> KeyStore<K, V> newKeyStore(Characteristic... characteristics) {
        Set<Characteristic> characteristicSet = new HashSet<>();
        Collections.addAll(characteristicSet, characteristics);
        return newKeyStore(characteristicSet);
    }

    public static <K, V> KeyStore<K, V> newKeyStore() {
        return newKeyStore(Characteristic.NO_DUPLICATIONS);
    }

    public static <K, V> SafeKeyStore<K, V> newSafeKeyStore(Set<Characteristic> characteristics) {
        if (characteristics.contains(Characteristic.THREAD_SAFE)) {
            ConcurrentMap<K, V> map = concurrentMapByCharacteristics(characteristics);
            return new ThreadSafeInMemoryKeyStore<>(map);
        } else {
            Map<K, V> map = mapByCharacteristics(characteristics);
            return new InMemoryKeyStore<>(map);
        }
    }

    public static <K, V> SafeKeyStore<K, V> newSafeKeyStore(Characteristic... characteristics) {
        Set<Characteristic> characteristicSet = new HashSet<>();
        Collections.addAll(characteristicSet, characteristics);
        return newSafeKeyStore(characteristicSet);
    }

    public static <K, V> SafeKeyStore<K, V> newSafeKeyStore() {
        return newSafeKeyStore(Characteristic.NO_DUPLICATIONS);
    }

    private static <T> Collection<T> collectionByCharacteristics(Set<Characteristic> characteristics) {
        Collection<T> collection;
        if (characteristics.contains(Characteristic.NO_DUPLICATIONS) && characteristics.contains(Characteristic.ORDERED)) {
            collection = new LinkedHashSet<>();
        } else if (characteristics.contains(Characteristic.NO_DUPLICATIONS)) {
            collection = new HashSet<>();
        } else {
            collection = new LinkedList<>();
        }

        return collection;
    }

    private static <K, V> Map<K, V> mapByCharacteristics(Set<Characteristic> characteristics) {
        Map<K, V> map;
        if (characteristics.contains(Characteristic.NO_DUPLICATIONS) && characteristics.contains(Characteristic.ORDERED)) {
            map = new LinkedHashMap<>();
        } else if (characteristics.contains(Characteristic.NO_DUPLICATIONS)) {
            map = new HashMap<>();
        } else {
            throw new IllegalArgumentException("Key store does not support duplication");
        }

        return map;
    }

    private static <K, V> ConcurrentMap<K, V> concurrentMapByCharacteristics(Set<Characteristic> characteristics) {
        ConcurrentMap<K, V> map;
        if (characteristics.contains(Characteristic.NO_DUPLICATIONS) && characteristics.contains(Characteristic.ORDERED)) {
            //noinspection SortedCollectionWithNonComparableKeys
            map = new ConcurrentSkipListMap<>();
        } else if (characteristics.contains(Characteristic.NO_DUPLICATIONS)) {
            map = new ConcurrentHashMap<>();
        } else {
            throw new IllegalArgumentException("Key store does not support duplication");
        }

        return map;
    }
}
