package com.castle.code.finder;

import com.castle.code.DefaultLibraryClassifier;
import com.castle.code.FileNativeLibrary;
import com.castle.code.LibraryClassifier;
import com.castle.code.NativeLibrary;
import com.castle.exceptions.FindException;
import com.castle.nio.PathMatching;
import com.castle.nio.PatternPathFinder;
import com.castle.util.os.Platform;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.io.IOException;
import java.nio.file.Path;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class DirectoryLibrarySearchPath implements LibrarySearchPath {

    private final Path mRoot;
    private final PatternPathFinder mPathFinder;
    private final LibraryPatternBuilder mPatternBuilder;
    private final LibraryClassifier mClassifier;

    public DirectoryLibrarySearchPath(Path path, LibraryPatternBuilder patternBuilder, LibraryClassifier classifier) {
        mRoot = path;
        mPathFinder = new PatternPathFinder(path.getFileSystem());
        mPatternBuilder = patternBuilder;
        mClassifier = classifier;
    }

    public DirectoryLibrarySearchPath(Path path) {
        this(path, new DefaultLibraryPatternBuilder(), new DefaultLibraryClassifier());
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
        try (Stream<Path> stream = mPathFinder.find(pattern, PathMatching.fileMatcher(), mRoot)) {
            for (Path path : stream.collect(Collectors.toSet())) {
                Platform platform = mClassifier.targetPlatform(path);
                if (platform.equals(targetPlatform)) {
                    return new FileNativeLibrary(path, platform);
                }
            }
        }

        throw new FindException("not found: " + pattern.pattern());
    }
}