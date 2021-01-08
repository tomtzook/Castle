package com.castle.util.os;

/**
 * <a href="https://github.com/openjdk/jdk/blob/9604ee82690f89320614b37bfef4178abc869777/src/java.base/windows/native/libjava/java_props_md.c">OpenJdk sources: /jdk/src/java.base/windows/native/libjava/java_props_md.c </a>
 * <a href="https://github.com/openjdk/jdk/blob/05a764f4ffb8030d6b768f2d362c388e5aabd92d/src/java.base/unix/native/libjava/java_props_md.c">OpenJdk sources: /jdk/src/java.base/unix/native/libjava/java_props_md.c</a>
 */
public enum KnownArchitecture implements Architecture {
    X86("x86", Bitness.BITS_32),
    I386("i386", Bitness.BITS_32),
    PPC("ppc", Bitness.BITS_32),
    X86_64("x86_64", Bitness.BITS_64),
    IA64("ia64", Bitness.BITS_64),
    AMD64("amd64", Bitness.BITS_64),
    AARCH64("aarch64", Bitness.BITS_64),
    PPC64LE("ppc64le", Bitness.BITS_64),
    UNKNOWN("unknown", null);
    ;

    private final String mOsArchName;
    private final Bitness mBitness;

    KnownArchitecture(String osArchName, Bitness bitness) {
        mOsArchName = osArchName;
        mBitness = bitness;
    }

    @Override
    public boolean isCurrent() {
        String arch = java.lang.System.getProperty("os.arch");
        return doesMatchProperty(arch);
    }

    @Override
    public Bitness bitness() {
        if (mBitness == null) {
            throw new IllegalStateException("has no bitness");
        }

        return mBitness;
    }

    boolean doesMatchProperty(String property) {
        if (mOsArchName == null) {
            return false;
        }

        return mOsArchName.equals(property);
    }
}
