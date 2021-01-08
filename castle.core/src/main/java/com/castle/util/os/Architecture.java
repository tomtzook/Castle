package com.castle.util.os;

public interface Architecture {

    String name();
    boolean isCurrent();
    Bitness bitness();
}
