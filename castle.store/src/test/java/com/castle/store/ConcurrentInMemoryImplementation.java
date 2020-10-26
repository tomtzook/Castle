package com.castle.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ConcurrentInMemoryImplementation implements KeyValueStoreTest.Implementation {

    static class Factory implements KeyValueStoreTest.ImplFactory {

        @Override
        public KeyValueStoreTest.Implementation create() {
            ConcurrentMap<Object, Object> values = new ConcurrentHashMap<>();
            KeyValueStore keyValueStore = new ConcurrentInMemoryKeyValueStore(values, new HashMap());
            return new ConcurrentInMemoryImplementation(keyValueStore, values);
        }

        @Override
        public KeyValueStoreTest.Implementation create(Map<?, ?> values) {
            ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>(values);
            KeyValueStore keyValueStore = new ConcurrentInMemoryKeyValueStore(map, new HashMap());
            return new ConcurrentInMemoryImplementation(keyValueStore, map);
        }
    }

    private final KeyValueStore mKeyValueStore;
    private final Map<Object, Object> mInnerStore;

    public ConcurrentInMemoryImplementation(KeyValueStore keyValueStore, Map<Object, Object> innerStore) {
        mKeyValueStore = keyValueStore;
        mInnerStore = innerStore;
    }

    @Override
    public KeyValueStore getImplementation() {
        return mKeyValueStore;
    }

    @Override
    public Map<Object, Object> getCurrentStoredValues() {
        return mInnerStore;
    }
}
