package com.castle.code.finder;

import com.castle.annotations.NotThreadSafe;
import com.castle.code.NativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.util.os.Platform;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.regex.Pattern;

@NotThreadSafe
public class CascadingSearchPath implements LibrarySearchPath {

    private final Collection<? extends LibrarySearchPath> mSearchPaths;

    public CascadingSearchPath(Collection<? extends LibrarySearchPath> searchPaths) {
        mSearchPaths = new HashSet<>(searchPaths);
    }

    @Override
    public NativeLibrary find(Platform targetPlatform, String name) throws IOException, FindException {
        ThrowableChain chain = Throwables.newChain();

        for (LibrarySearchPath searchPath : mSearchPaths) {
            try {
                return searchPath.find(targetPlatform, name);
            } catch (IOException | FindException e) {
                chain.chain(e);
            }
        }

        throw new FindException("not found: " + name,
                chain.getTopThrowable().orElse(null));
    }

    @Override
    public NativeLibrary find(Platform targetPlatform, Pattern pattern) throws IOException, FindException {
        ThrowableChain chain = Throwables.newChain();

        for (LibrarySearchPath searchPath : mSearchPaths) {
            try {
                return searchPath.find(targetPlatform, pattern);
            } catch (IOException | FindException e) {
                chain.chain(e);
            }
        }

        throw new FindException("not found: " + pattern.pattern(),
                chain.getTopThrowable().orElse(null));
    }
}
