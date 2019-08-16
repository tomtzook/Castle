package com.castle.testutil;

import com.castle.nio.temp.TempPathGenerator;
import org.junit.rules.TemporaryFolder;

public class TemporaryPaths {

    public static TempPathGenerator pathGenerator(TemporaryFolder temporaryFolder) {
        return new TempPathGenerator(temporaryFolder.getRoot().toPath(), "", "");
    }
}
