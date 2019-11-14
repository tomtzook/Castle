package com.castle.testutil.collections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapUtils {

    private MapUtils() {}

    public static <K, V> Map<K, V> generateMapWithKeys(Collection<? extends K> keys, Supplier<? extends V> valueGenerator) {
        Map<K, V> map = new HashMap<>();
        keys.forEach((key)->map.put(key, valueGenerator.get()));

        return map;
    }
}
