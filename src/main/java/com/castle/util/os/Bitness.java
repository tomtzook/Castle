package com.castle.util.os;

public enum Bitness {
    X86,
    X64;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
