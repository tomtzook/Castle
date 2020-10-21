package com.castle.util.os;

public enum OperatingSystem {
    Windows {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.contains("win");
        }
    },
    Linux {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.contains("linux");
        }
    },
    Mac {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.contains("max");
        }
    },
    Solaris {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.contains("sunos");
        }
    };

    public abstract boolean doesNameMatch(String osName);
}
