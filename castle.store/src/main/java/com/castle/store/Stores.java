package com.castle.store;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

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

    private static <T> Collection<T> collectionByCharacteristics(Set<Characteristic> characteristics) {
        Collection<T> collection;
        if (characteristics.contains(Characteristic.NO_DUPLICATIONS) && characteristics.contains(Characteristic.ORDERED)) {
            collection = new LinkedHashSet<>();
        } else if (characteristics.contains(Characteristic.NO_DUPLICATIONS)) {
            collection = new HashSet<>();
        } else {
            collection = new ArrayList<>();
        }

        return collection;
    }
}
