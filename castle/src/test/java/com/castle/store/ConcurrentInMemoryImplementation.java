package com.castle.store;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ConcurrentInMemoryImplementation implements StoreTest.Implementation {

    static class Factory implements StoreTest.ImplFactory {

        @Override
        public StoreTest.Implementation create() {
            ConcurrentMap<Object, Object> values = new ConcurrentHashMap<>();
            Store store = new ConcurrentInMemoryStore(values, new HashMap());
            return new ConcurrentInMemoryImplementation(store, values);
        }

        @Override
        public StoreTest.Implementation create(Map<?, ?> values) {
            ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>(values);
            Store store = new ConcurrentInMemoryStore(map, new HashMap());
            return new ConcurrentInMemoryImplementation(store, map);
        }
    }

    private final Store mStore;
    private final Map<Object, Object> mInnerStore;

    public ConcurrentInMemoryImplementation(Store store, Map<Object, Object> innerStore) {
        mStore = store;
        mInnerStore = innerStore;
    }

    @Override
    public Store getImplementation() {
        return mStore;
    }

    @Override
    public Map<Object, Object> getCurrentStoredValues() {
        return mInnerStore;
    }
}
