package com.castle.util.os;

public class Architecture {

    private static final String X86_ARCH = "x86";

    public Bitness getBitness() {
        String property = System.getProperty("os.arch");
        if (X86_ARCH.equalsIgnoreCase(property)) {
            return Bitness.X86;
        }

        return Bitness.X64;
    }
}
