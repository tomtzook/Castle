package com.castle.util.os;

public enum KnownOperatingSystem implements OperatingSystem {
    WINDOWS("win", ".dll"),
    LINUX("linux", ".so"),
    MAC("mac", ".dylib"),
    SOLARIS("sunos", ".so"),
    UNKNOWN(null, null)
    ;

    private final String mOsName;
    private final String mNativeLibExt;

    KnownOperatingSystem(String osName, String nativeLibExt) {
        mOsName = osName;
        mNativeLibExt = nativeLibExt;
    }

    @Override
    public boolean isCurrent() {
        String osName = java.lang.System.getProperty("os.name").toLowerCase();
        return doesMatchProperty(osName);
    }

    @Override
    public String nativeLibraryExtension() {
        return mNativeLibExt;
    }

    boolean doesMatchProperty(String fullOsName) {
        if (mOsName == null) {
            return false;
        }

        return fullOsName.contains(mOsName);
    }
}
