package com.castle.reflect.data;

public class GenericDataType<K> implements DataType<K> {

    private final K mKey;

    public GenericDataType(K key) {
        mKey = key;
    }

    @Override
    public boolean matchesKey(K key) {
        return mKey == key;
    }

    protected K getKey() {
        return mKey;
    }
}
