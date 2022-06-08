package com.castle.nio.zip;

import com.castle.util.closeables.ReferenceCounter;

import java.nio.file.FileSystem;
import java.util.concurrent.locks.Lock;

public interface ToOpenZip<T extends OpenZip> {

    T newInstance(FileSystem zipFileSystem, ReferenceCounter referenceCounter, Lock closeLock);
}
