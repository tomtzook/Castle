package com.castle.util.registration;

import java.util.Collection;

public interface Registry<T> {

    boolean register(T t);
    Collection<T> getRegistered(boolean clearRegistry);
}
