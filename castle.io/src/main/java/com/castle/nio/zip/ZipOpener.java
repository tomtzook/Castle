package com.castle.nio.zip;

import com.castle.util.closeables.ReferenceCounter;

import java.io.IOException;
import java.util.concurrent.locks.Lock;

public interface ZipOpener<T extends OpenZip> {

    T open(ReferenceCounter referenceCounter, Lock closeLock) throws IOException;
}
