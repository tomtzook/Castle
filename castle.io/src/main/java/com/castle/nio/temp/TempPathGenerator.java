package com.castle.nio.temp;

import com.castle.annotations.Immutable;
import com.castle.nio.SystemPaths;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.spi.FileSystemProvider;
import java.util.EnumSet;
import java.util.Objects;
import java.util.Random;

@Immutable
public class TempPathGenerator {

    public static class Builder {

        private final Path mParent;

        private FileSystemProvider mFileSystemProvider;
        private Random mRandom;
        private String mPrefix;
        private String mSuffix;

        public Builder(Path parent) {
            mParent = parent;

            mFileSystemProvider = mParent.getFileSystem().provider();
            mRandom = new Random();
            mPrefix = "";
            mSuffix = "";
        }

        public Builder withProvider(FileSystemProvider provider) {
            mFileSystemProvider = Objects.requireNonNull(provider, "provider");
            return this;
        }

        public Builder withRandomGenerator(Random random) {
            mRandom = Objects.requireNonNull(random, "random");
            return this;
        }

        public Builder withPrefix(String prefix) {
            mPrefix = Objects.requireNonNull(prefix, "prefix");
            return this;
        }

        public Builder withSuffix(String suffix) {
            mSuffix = Objects.requireNonNull(suffix, "suffix");
            return this;
        }

        public TempPathGenerator build() {
            return new TempPathGenerator(mFileSystemProvider, mParent, mRandom, mPrefix, mSuffix);
        }
    }

    private final FileSystemProvider mFileSystemProvider;
    private final Path mParent;
    private final Random mRandom;
    private final String mPrefix;
    private final String mSuffix;

    public TempPathGenerator(FileSystemProvider fileSystemProvider, Path parent, Random random, String prefix, String suffix) {
        mFileSystemProvider = fileSystemProvider;
        mParent = parent;
        mRandom = random;
        mPrefix = prefix;
        mSuffix = suffix;
    }

    public TempPathGenerator(Path parent, Random random, String prefix, String suffix) {
        this(parent.getFileSystem().provider(), parent, random, prefix, suffix);
    }

    public TempPathGenerator(Path parent, String prefix, String suffix) {
        this(parent, new Random(), prefix, suffix);
    }

    public TempPathGenerator(String prefix, String suffix) {
        this(SystemPaths.tempDirectory(), prefix, suffix);
    }

    public TempPathGenerator() {
        this("temp_", "");
    }

    public TempPath generateFile(FileAttribute<?>... attributes) throws IOException {
        Path path = generatePath();
        createFile(path, attributes);

        return new TempPath(mFileSystemProvider, path);
    }

    public TempPath generateDirectory(FileAttribute<?>... attributes) throws IOException {
        Path path = generatePath();
        mFileSystemProvider.createDirectory(path, attributes);

        return new TempPath(mFileSystemProvider, path);
    }

    public Path generatePath() {
        String name = generateRandomName();
        return mParent.resolve(name);
    }

    private void createFile(Path path, FileAttribute<?>... attributes) throws IOException {
        EnumSet<StandardOpenOption> options =
                EnumSet.of(StandardOpenOption.CREATE_NEW, StandardOpenOption.WRITE);

        mFileSystemProvider.newByteChannel(path, options, attributes).close();
    }

    private String generateRandomName() {
        long random = mRandom.nextLong();
        return String.format("%s%d%s", mPrefix, random, mSuffix);
    }
}
