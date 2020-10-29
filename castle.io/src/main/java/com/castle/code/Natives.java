package com.castle.code;

import com.castle.annotations.NotThreadSafe;
import com.castle.annotations.Stateless;
import com.castle.code.finder.ArchivedNativeLibraryFinder;
import com.castle.code.finder.MultiNativeLibraryFinder;
import com.castle.code.finder.NativeLibraryFinder;
import com.castle.code.loader.NativeLibraryLoader;
import com.castle.code.loader.TempNativeLibraryLoader;
import com.castle.exceptions.CodeLoadException;
import com.castle.exceptions.FindException;
import com.castle.nio.zip.Zip;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

@Stateless
public class Natives {

    private Natives() {
    }

    public static NativeLibraryFinder finderForArchives(Zip... zips) {
        Collection<NativeLibraryFinder> nativeLibraryFinders = new ArrayList<>();
        for (Zip zip : zips) {
            nativeLibraryFinders.add(new ArchivedNativeLibraryFinder(zip));
        }

        return new MultiNativeLibraryFinder(nativeLibraryFinders);
    }

    public static NativeLibraryFinder finderForArchives(Path baseSearchPath, Zip... zips) {
        Collection<NativeLibraryFinder> nativeLibraryFinders = new ArrayList<>();
        for (Zip zip : zips) {
            nativeLibraryFinders.add(new ArchivedNativeLibraryFinder(zip, baseSearchPath));
        }

        return new MultiNativeLibraryFinder(nativeLibraryFinders);
    }

    public static Loader startLoading(NativeLibraryLoader loader) {
        return new Loader(loader);
    }

    public static Loader startLoading() {
        return new Loader(defaultLoader());
    }

    public static NativeLibraryLoader defaultLoader() {
        return new TempNativeLibraryLoader();
    }

    @NotThreadSafe
    public static class Loader {

        private final NativeLibraryLoader mLoader;
        private final Collection<NativeLibraryFinder> mFinders;

        public Loader(NativeLibraryLoader loader) {
            mLoader = loader;
            mFinders = new ArrayList<>();
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

        public void loadAll(String... names) throws FindException, CodeLoadException {
            loadAll(Arrays.asList(names));
        }

        public void loadAll(Collection<String> names) throws FindException, CodeLoadException {
            NativeLibraryFinder finder = new MultiNativeLibraryFinder(mFinders);

            for (String name : names) {
                NativeLibrary nativeLibrary = finder.find(name);
                mLoader.load(nativeLibrary);
            }
        }
    }
}
