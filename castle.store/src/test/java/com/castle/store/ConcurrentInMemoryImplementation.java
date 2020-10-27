package com.castle.store;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class ConcurrentInMemoryImplementation implements KeyStoreTest.Implementation {

    static class Factory implements KeyStoreTest.ImplFactory {

        @Override
        public KeyStoreTest.Implementation create() {
            ConcurrentMap<Object, Object> values = new ConcurrentHashMap<>();
            KeyStore keyStore = new ThreadSafeInMemoryKeyStore(values);
            return new ConcurrentInMemoryImplementation(keyStore, values);
        }

        @Override
        public KeyStoreTest.Implementation create(Map<?, ?> values) {
            ConcurrentMap<Object, Object> map = new ConcurrentHashMap<>(values);
            KeyStore keyStore = new ThreadSafeInMemoryKeyStore(map);
            return new ConcurrentInMemoryImplementation(keyStore, map);
        }
    }

    private final KeyStore mKeyStore;
    private final Map<Object, Object> mInnerStore;

    public ConcurrentInMemoryImplementation(KeyStore keyStore, Map<Object, Object> innerStore) {
        mKeyStore = keyStore;
        mInnerStore = innerStore;
    }

    @Override
    public KeyStore getImplementation() {
        return mKeyStore;
    }

    @Override
    public Map<Object, Object> getCurrentStoredValues() {
        return mInnerStore;
    }
}
