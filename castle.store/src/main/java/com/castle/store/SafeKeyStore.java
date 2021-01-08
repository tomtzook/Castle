package com.castle.store;

public interface SafeKeyStore<K, V> extends KeyStore<K, V>, SafeReadableKeyStore<K, V>, SafeWwritableKeyStore<K, V> {

}
