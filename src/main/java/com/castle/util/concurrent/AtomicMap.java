package com.castle.util.concurrent;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class AtomicMap<K, V> {

    private final Map<K, V> mMap;
    private final ReadWriteLock mLock;

    public AtomicMap(Map<K, V> map, ReadWriteLock lock) {
        mMap = map;
        mLock = lock;
    }

    public AtomicMap(Map<K, V> map) {
        this(map, new ReentrantReadWriteLock());
    }

    public boolean contains(K key) {
        mLock.readLock().lock();
        try {
            return mMap.containsKey(key);
        } finally {
            mLock.readLock().unlock();
        }
    }

    public boolean contains(Collection<? extends K> keys) {
        mLock.readLock().lock();
        try {
            return mMap.keySet().containsAll(keys);
        } finally {
            mLock.readLock().unlock();
        }
    }

    public V get(K key) {
        mLock.readLock().lock();
        try {
            return mMap.get(key);
        } finally {
            mLock.readLock().unlock();
        }
    }

    public Map<K, V> copy() {
        mLock.readLock().lock();
        try {
            return new HashMap<>(mMap);
        } finally {
            mLock.readLock().unlock();
        }
    }

    public void put(Map<? extends K, ? extends V> values) {
        mLock.writeLock().lock();
        try {
            mMap.putAll(values);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public V remove(K key) {
        mLock.writeLock().lock();
        try {
            return mMap.remove(key);
        } finally {
            mLock.writeLock().unlock();
        }
    }

    public Map<K, V> removeAll(Collection<? extends K> keys) {
        Map<K, V> removed = new HashMap<>(keys.size());

        mLock.writeLock().lock();
        try {
            for (K key : keys) {
                V value = mMap.remove(key);
                if (value != null) {
                    removed.put(key, value);
                }
            }
        } finally {
            mLock.writeLock().unlock();
        }

        return removed;
    }

    public void clear() {
        mLock.writeLock().lock();
        try {
            mMap.clear();
        } finally {
            mLock.writeLock().unlock();
        }
    }
}
