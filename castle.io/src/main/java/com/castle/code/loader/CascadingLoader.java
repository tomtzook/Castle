package com.castle.code.loader;

import com.castle.code.NativeLibrary;
import com.castle.exceptions.CodeLoadException;

import java.util.Collection;
import java.util.HashSet;

public class CascadingLoader implements NativeLibraryLoader {

    private final Collection<? extends NativeLibraryLoader> mLoaders;

    public CascadingLoader(Collection<? extends NativeLibraryLoader> loaders) {
        mLoaders = new HashSet<>(loaders);
    }

    @Override
    public boolean supports(NativeLibrary nativeLibrary) {
        for (NativeLibraryLoader loader : mLoaders) {
            if (loader.supports(nativeLibrary)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public void load(NativeLibrary nativeLibrary) throws CodeLoadException {
        for (NativeLibraryLoader loader : mLoaders) {
            if (loader.supports(nativeLibrary)) {
                loader.load(nativeLibrary);
                return;
            }
        }

        throw new IllegalArgumentException("No loader which supports library " + nativeLibrary);
    }
}
