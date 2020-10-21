package com.castle.code.finder;

import com.castle.annotations.NotThreadSafe;
import com.castle.code.NativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.util.Collection;

@NotThreadSafe
public class MultiNativeLibraryFinder implements NativeLibraryFinder {

    private final Collection<NativeLibraryFinder> mLibraryFinders;

    public MultiNativeLibraryFinder(Collection<NativeLibraryFinder> libraryFinders) {
        mLibraryFinders = libraryFinders;
    }

    @Override
    public NativeLibrary find(String name) throws FindException {
        ThrowableChain chain = Throwables.newChain();
        for (NativeLibraryFinder nativeLibraryFinder : mLibraryFinders) {
            try {
                return nativeLibraryFinder.find(name);
            } catch (FindException e) {
                chain.chain(e);
            }
        }

        chain.throwIfType(FindException.class);
        throw chain.toRuntime(() -> new RuntimeException("Should have returned an exception"));
    }
}
