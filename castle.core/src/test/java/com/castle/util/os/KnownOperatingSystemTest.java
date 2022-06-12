package com.castle.util.os;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.DisabledOnOs;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import static org.junit.jupiter.api.Assertions.*;

class KnownOperatingSystemTest {

    @Test
    @EnabledOnOs({OS.WINDOWS})
    public void isCurrent_osWindows_true() throws Throwable {
        assertTrue(KnownOperatingSystem.WINDOWS.isCurrent());
    }

    @Test
    @DisabledOnOs({OS.WINDOWS})
    public void isCurrent_osNotWindows_false() throws Throwable {
        assertFalse(KnownOperatingSystem.WINDOWS.isCurrent());
    }

    @Test
    @EnabledOnOs({OS.LINUX})
    public void isCurrent_osLinux_true() throws Throwable {
        assertTrue(KnownOperatingSystem.LINUX.isCurrent());
    }

    @Test
    @DisabledOnOs({OS.LINUX})
    public void isCurrent_osNotLinux_false() throws Throwable {
        assertFalse(KnownOperatingSystem.LINUX.isCurrent());
    }

    @Test
    @EnabledOnOs({OS.MAC})
    public void isCurrent_osMac_true() throws Throwable {
        assertTrue(KnownOperatingSystem.MAC.isCurrent());
    }

    @Test
    @DisabledOnOs({OS.MAC})
    public void isCurrent_osNotMac_false() throws Throwable {
        assertFalse(KnownOperatingSystem.MAC.isCurrent());
    }

    @Test
    @EnabledOnOs({OS.SOLARIS})
    public void isCurrent_osSolaris_true() throws Throwable {
        assertTrue(KnownOperatingSystem.SOLARIS.isCurrent());
    }

    @Test
    @DisabledOnOs({OS.SOLARIS})
    public void isCurrent_osNotSolaris_false() throws Throwable {
        assertFalse(KnownOperatingSystem.SOLARIS.isCurrent());
    }
}