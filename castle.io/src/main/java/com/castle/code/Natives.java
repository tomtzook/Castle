package com.castle.code;

import com.castle.annotations.NotThreadSafe;
import com.castle.annotations.Stateless;
import com.castle.code.finder.ArchivedNativeLibraryFinder;
import com.castle.code.finder.CascadingFinder;
import com.castle.code.finder.FileNativeLibraryFinder;
import com.castle.code.finder.NativeLibraryFinder;
import com.castle.code.loader.CascadingLoader;
import com.castle.code.loader.FileNativeLibraryLoader;
import com.castle.code.loader.NativeLibraryLoader;
import com.castle.code.loader.TempNativeLibraryLoader;
import com.castle.exceptions.CodeLoadException;
import com.castle.exceptions.FindException;
import com.castle.nio.zip.Zip;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;

@Stateless
public class Natives {

    private Natives() {
    }

    public static NativeLibraryFinder finderForArchives(Zip... zips) {
        Collection<NativeLibraryFinder> nativeLibraryFinders = new ArrayList<>();
        for (Zip zip : zips) {
            nativeLibraryFinders.add(new ArchivedNativeLibraryFinder(zip));
        }

        return new CascadingFinder(nativeLibraryFinders);
    }

    public static NativeLibraryFinder finderForArchives(Path baseSearchPath, Zip... zips) {
        Collection<NativeLibraryFinder> nativeLibraryFinders = new ArrayList<>();
        for (Zip zip : zips) {
            nativeLibraryFinders.add(new ArchivedNativeLibraryFinder(zip, baseSearchPath));
        }

        return new CascadingFinder(nativeLibraryFinders);
    }

    public static NativeLibraryFinder javaLibraryPathFinder() {
        String path = System.getProperty("java.library.path");
        Collection<Path> paths = Arrays.stream(path.split(File.pathSeparator))
                .map(Paths::get)
                .collect(Collectors.toList());

        return new FileNativeLibraryFinder(paths);
    }

    public static Loader startLoading() {
        return new Loader()
                .from(javaLibraryPathFinder())
                .withLoader(new FileNativeLibraryLoader())
                .withLoader(new TempNativeLibraryLoader());
    }

    @NotThreadSafe
    public static class Loader {

        private final Collection<NativeLibraryFinder> mFinders;
        private final Collection<NativeLibraryLoader> mLoaders;

        public Loader() {
            mFinders = new ArrayList<>();
            mLoaders = new ArrayList<>();
        }

        public Loader from(NativeLibraryFinder finder) {
            mFinders.add(finder);
            return this;
        }

        public Loader from(Zip zip, Path basePath) {
            return from(new ArchivedNativeLibraryFinder(zip, basePath));
        }

        public Loader from(Zip zip) {
            return from(new ArchivedNativeLibraryFinder(zip));
        }

        public Loader withLoader(NativeLibraryLoader loader) {
            mLoaders.add(loader);
            return this;
        }

        public void loadAll(String... names) throws FindException, CodeLoadException {
            loadAll(Arrays.asList(names));
        }

        public void loadAll(Collection<String> names) throws FindException, CodeLoadException {
            NativeLibraryFinder finder = new CascadingFinder(mFinders);
            NativeLibraryLoader loader = new CascadingLoader(mLoaders);

            for (String name : names) {
                NativeLibrary nativeLibrary = finder.find(name);
                if (!loader.supports(nativeLibrary)) {
                    throw new IllegalArgumentException("Unsupported library: " + nativeLibrary);
                }
                loader.load(nativeLibrary);
            }
        }
    }
}
