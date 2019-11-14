package com.castle.testutil.collections;

import java.util.ArrayList;
import java.util.Collection;

public class CollectionUtils {

    private CollectionUtils() {}

    public static <T> Collection<T> merge(Collection<? extends T> collection1, Collection<? extends T> collection2) {
        Collection<T> collection = new ArrayList<>(collection1.size() + collection2.size());
        collection.addAll(collection1);
        collection.addAll(collection2);

        return collection;
    }
}
