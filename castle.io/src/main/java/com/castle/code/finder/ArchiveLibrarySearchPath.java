package com.castle.code.finder;

import com.castle.code.ArchivedNativeLibrary;
import com.castle.code.DefaultNativeCodeClassifier;
import com.castle.code.NativeCodeClassifier;
import com.castle.code.NativeLibrary;
import com.castle.code.UnableToClassifyException;
import com.castle.exceptions.FindException;
import com.castle.nio.PathMatching;
import com.castle.nio.zip.OpenZip;
import com.castle.nio.zip.Zip;
import com.castle.util.os.Platform;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ArchiveLibrarySearchPath implements LibrarySearchPath {

    private final Zip mZip;
    private final LibraryPatternBuilder mPatternBuilder;
    private final NativeCodeClassifier mClassifier;

    public ArchiveLibrarySearchPath(Zip zip, LibraryPatternBuilder patternBuilder, NativeCodeClassifier classifier) {
        mZip = zip;
        mPatternBuilder = patternBuilder;
        mClassifier = classifier;
    }

    public ArchiveLibrarySearchPath(Zip zip) {
        this(zip, new DefaultLibraryPatternBuilder(), new DefaultNativeCodeClassifier());
    }

    public ArchiveLibrarySearchPath(Path path) {
        this(Zip.fromPath(path));
    }

    @Override
    public NativeLibrary find(Platform targetPlatform, String name) throws IOException, FindException {
        ThrowableChain chain = Throwables.newChain();

        try {
            Pattern pattern = mPatternBuilder.build(targetPlatform, name);
            return find(targetPlatform, pattern);
        } catch (FindException | IOException | UncheckedIOException e) {
            chain.chain(e);
        }

        try {
            Pattern pattern = mPatternBuilder.build(name);
            return find(targetPlatform, pattern);
        } catch (FindException | IOException | UncheckedIOException e) {
            chain.chain(e);
        }

        chain.throwIfType(FindException.class);
        chain.throwIfType(IOException.class);
        throw chain.toRuntime(()-> new RuntimeException("should not have reached"));
    }

    @Override
    public NativeLibrary find(Platform targetPlatform, Pattern pattern) throws IOException, FindException {
        ThrowableChain chain = Throwables.newChain();

        try (OpenZip openZip = mZip.open();
             Stream<Path> stream = openZip.pathFinder().find(pattern, PathMatching.fileMatcher())) {
            for (Path path : stream.collect(Collectors.toSet())) {
                try {
                    Platform platform = mClassifier.targetPlatform(path);
                    if (platform.equals(targetPlatform)) {
                        return new ArchivedNativeLibrary(platform, mZip, path.toAbsolutePath().toString());
                    }
                } catch (UnableToClassifyException e) {
                    chain.chain(e);
                }
            }
        }

        throw new FindException("not found: " + pattern.pattern(),
                chain.getTopThrowable().orElse(null));
    }
}
