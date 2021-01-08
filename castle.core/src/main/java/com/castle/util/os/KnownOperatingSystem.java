package com.castle.util.os;

public enum KnownOperatingSystem implements OperatingSystem {
    WINDOWS("win"),
    LINUX("linux"),
    MAC("max"),
    SOLARIS("sunos"),
    UNKNOWN(null)
    ;

    private final String mOsName;

    KnownOperatingSystem(String osName) {
        mOsName = osName;
    }

    @Override
    public boolean isCurrent() {
        String osName = java.lang.System.getProperty("os.name").toLowerCase();
        return doesMatchProperty(osName);
    }

    boolean doesMatchProperty(String fullOsName) {
        if (mOsName == null) {
            return false;
        }

        return fullOsName.contains(mOsName);
    }
}
