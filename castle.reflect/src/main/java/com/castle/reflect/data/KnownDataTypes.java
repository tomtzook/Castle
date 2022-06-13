package com.castle.reflect.data;

import java.util.Arrays;
import java.util.Collection;
import java.util.NoSuchElementException;

public interface KnownDataTypes<K, T extends DataType<K>> {

    T getFromKey(K key);

    static <K, T extends DataType<K>> KnownDataTypes<K, T> of(Collection<? extends T> types) {
        return new Impl<>(types);
    }

    @SafeVarargs
    static <K, T extends DataType<K>> KnownDataTypes<K, T> of(T... types) {
        return of(Arrays.asList(types));
    }

    class Impl<K, T extends DataType<K>> implements KnownDataTypes<K, T> {

        private final Collection<? extends T> mTypes;

        public Impl(Collection<? extends T> types) {
            mTypes = types;
        }

        @Override
        public T getFromKey(K key) {
            for (T type : mTypes) {
                if (type.matchesKey(key)) {
                    return type;
                }
            }

            throw new NoSuchElementException("Unknown type: " + key);
        }
    }
}
