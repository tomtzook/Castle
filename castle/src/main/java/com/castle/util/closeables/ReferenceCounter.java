package com.castle.util.closeables;

public interface ReferenceCounter {

    void increment();
    boolean decrement();
}
