package com.castle.util.os;

public enum Platform {
    Windows {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.indexOf("win") > 0;
        }
    },
    Unix {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.indexOf("nix") > 0;
        }
    },
    Mac {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.indexOf("max") > 0;
        }
    },
    Solaris {
        @Override
        public boolean doesNameMatch(String osName) {
            return osName.indexOf("sunos") > 0;
        }
    };

    public abstract boolean doesNameMatch(String osName);
}
