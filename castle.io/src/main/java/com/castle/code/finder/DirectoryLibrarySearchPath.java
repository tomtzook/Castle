package com.castle.code.finder;

import com.castle.code.DefaultNativeCodeClassifier;
import com.castle.code.FileNativeLibrary;
import com.castle.code.NativeCodeClassifier;
import com.castle.code.NativeLibrary;
import com.castle.code.UnableToClassifyException;
import com.castle.exceptions.FindException;
import com.castle.nio.PathMatching;
import com.castle.nio.PatternPathFinder;
import com.castle.util.os.Platform;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryLibrarySearchPath implements LibrarySearchPath {

    private final Path mRoot;
    private final PatternPathFinder mPathFinder;
    private final LibraryPatternBuilder mPatternBuilder;
    private final NativeCodeClassifier mClassifier;

    public DirectoryLibrarySearchPath(Path path, LibraryPatternBuilder patternBuilder, NativeCodeClassifier classifier) {
        mRoot = path;
        mPathFinder = new PatternPathFinder(path.getFileSystem());
        mPatternBuilder = patternBuilder;
        mClassifier = classifier;
    }

    public DirectoryLibrarySearchPath(Path path) {
        this(path, new DefaultLibraryPatternBuilder(), new DefaultNativeCodeClassifier());
    }

    @Override
    public NativeLibrary find(Platform targetPlatform, String name) throws FindException, IOException {
        ThrowableChain chain = Throwables.newChain();

        try {
            Pattern pattern = mPatternBuilder.build(targetPlatform, name);
            return find(targetPlatform, pattern);
        } catch (FindException | IOException e) {
            chain.chain(e);
        }

        try {
            Pattern pattern = mPatternBuilder.build(name);
            return find(targetPlatform, pattern);
        } catch (FindException | IOException e) {
            chain.chain(e);
        }

        chain.throwIfType(FindException.class);
        chain.throwIfType(IOException.class);
        throw chain.toRuntime(()-> new RuntimeException("should not have reached"));
    }

    @Override
    public NativeLibrary find(Platform targetPlatform, Pattern pattern) throws FindException, IOException {
        ThrowableChain chain = Throwables.newChain();

        try (Stream<Path> stream = mPathFinder.find(pattern, PathMatching.fileMatcher(), mRoot)) {
            for (Path path : stream.collect(Collectors.toSet())) {
                try {
                    Platform platform = mClassifier.targetPlatform(path);
                    if (platform.equals(targetPlatform)) {
                        return new FileNativeLibrary(path, platform);
                    }
                } catch (UnableToClassifyException e) {
                    chain.chain(e);
                }
            }
        } catch (UncheckedIOException e) {
            throw new IOException(e);
        }

        throw new FindException("not found: " + pattern.pattern(), chain.getTopThrowable().orElse(null));
    }
}
