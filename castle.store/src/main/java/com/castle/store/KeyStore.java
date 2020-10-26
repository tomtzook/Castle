package com.castle.store;

import com.castle.store.exceptions.StoreException;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;

public interface KeyStore<K, V> extends ReadableKeyStore<K, V>, WritableKeyStore<K, V> {

}
