package com.castle.code;

import com.castle.annotations.NotThreadSafe;
import com.castle.annotations.Stateless;
import com.castle.code.finder.ArchiveLibrarySearchPath;
import com.castle.code.finder.CascadingSearchPath;
import com.castle.code.finder.DirectoryLibrarySearchPath;
import com.castle.code.finder.LibrarySearchPath;
import com.castle.code.loader.CascadingLoader;
import com.castle.code.loader.FileNativeLibraryLoader;
import com.castle.code.loader.NativeLibraryLoader;
import com.castle.code.loader.TempNativeLibraryLoader;
import com.castle.exceptions.CodeLoadException;
import com.castle.exceptions.FindException;
import com.castle.nio.zip.Zip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Stateless
public class Natives {

    private Natives() {
    }

    public static LibrarySearchPath javaLibraryPathFinder() {
        String path = System.getProperty("java.library.path");
        Collection<LibrarySearchPath> paths = Arrays.stream(path.split(File.pathSeparator))
                .map(Paths::get)
                .map(DirectoryLibrarySearchPath::new)
                .collect(Collectors.toList());

        return new CascadingSearchPath(paths);
    }

    public static LibrarySearchPath javaClassPathFinder() {
        String path = System.getProperty("java.class.path");
        Collection<LibrarySearchPath> paths = Arrays.stream(path.split(File.pathSeparator))
                .map(Paths::get)
                .map(DirectoryLibrarySearchPath::new)
                .collect(Collectors.toList());

        return new CascadingSearchPath(paths);
    }

    public static Loader newLoader() {
        return new Loader()
                .from(javaLibraryPathFinder())
                .from(javaClassPathFinder())
                .withLoader(new FileNativeLibraryLoader())
                .withLoader(new TempNativeLibraryLoader());
    }

    @NotThreadSafe
    public static class Loader {

        private final Collection<LibrarySearchPath> mSearchPaths;
        private final Collection<NativeLibraryLoader> mLoaders;

        public Loader() {
            mSearchPaths = new ArrayList<>();
            mLoaders = new ArrayList<>();
        }

        public Loader from(LibrarySearchPath searchPath) {
            mSearchPaths.add(searchPath);
            return this;
        }

        public Loader from(Zip zip) {
            return from(new ArchiveLibrarySearchPath(zip));
        }

        public Loader withLoader(NativeLibraryLoader loader) {
            mLoaders.add(loader);
            return this;
        }

        public void load(String... names) throws FindException, CodeLoadException, IOException {
            load(Arrays.asList(names));
        }

        public void load(Collection<String> names) throws FindException, CodeLoadException, IOException {
            LibrarySearchPath searchPath = new CascadingSearchPath(mSearchPaths);
            NativeLibraryLoader loader = new CascadingLoader(mLoaders);

            for (String name : names) {
                NativeLibrary nativeLibrary = searchPath.find(name);
                if (!loader.supports(nativeLibrary)) {
                    throw new IllegalArgumentException("Unsupported library: " + nativeLibrary);
                }
                loader.load(nativeLibrary);
            }
        }
    }
}
