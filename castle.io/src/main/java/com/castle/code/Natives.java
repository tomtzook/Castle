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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Stateless
public class Natives {

    private Natives() {
    }

    public static LibrarySearchPath javaLibraryPathFinder() {
        String path = System.getProperty("java.library.path");
        Collection<LibrarySearchPath> paths = Arrays.stream(path.split(File.pathSeparator))
                .map(Paths::get)
                .map(Natives::newSearchPath)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new CascadingSearchPath(paths);
    }

    public static LibrarySearchPath javaClassPathFinder() {
        String path = System.getProperty("java.class.path");
        Collection<LibrarySearchPath> paths = Arrays.stream(path.split(File.pathSeparator))
                .map(Paths::get)
                .map(Natives::newSearchPath)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return new CascadingSearchPath(paths);
    }

    private static LibrarySearchPath newSearchPath(Path path) {
        if (!Files.exists(path)) {
            return null;
        } else if (Files.isDirectory(path)) {
            return new DirectoryLibrarySearchPath(path);
        } else if (Files.isRegularFile(path)) {
            return new ArchiveLibrarySearchPath(path);
        } else {
            throw new IllegalArgumentException("Unable to create search path for path");
        }
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

        private final List<LibrarySearchPath> mSearchPaths;
        private final List<NativeLibraryLoader> mLoaders;

        public Loader() {
            mSearchPaths = new ArrayList<>();
            mLoaders = new ArrayList<>();
        }

        public Loader from(LibrarySearchPath searchPath) {
            mSearchPaths.add(searchPath);
            return this;
        }

        public Loader from(Path path) {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("expected directory");
            }

            return from(new DirectoryLibrarySearchPath(path));
        }

        public Loader from(Zip zip) {
            return from(new ArchiveLibrarySearchPath(zip));
        }

        public Loader firstFrom(LibrarySearchPath searchPath) {
            mSearchPaths.add(0, searchPath);
            return this;
        }

        public Loader firstFrom(Path path) {
            if (!Files.isDirectory(path)) {
                throw new IllegalArgumentException("expected directory");
            }

            return firstFrom(new DirectoryLibrarySearchPath(path));
        }

        public Loader firstFrom(Zip zip) {
            return firstFrom(new ArchiveLibrarySearchPath(zip));
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
