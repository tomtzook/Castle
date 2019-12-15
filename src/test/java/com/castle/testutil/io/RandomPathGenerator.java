package com.castle.testutil.io;

import com.castle.nio.temp.TempPathGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Random;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomPathGenerator {

    private final Path mDirectory;
    private final Random mRandom;

    public RandomPathGenerator(Path directory) {
        mDirectory = directory;
        mRandom = new Random();
    }

    public Collection<Path> createFiles(String baseName, int count) throws IOException {
        final TempPathGenerator pathGenerator = new TempPathGenerator(mDirectory, baseName, "");

        IntFunction<Path> function = i -> {
            try {
                return pathGenerator.generateFile().originalPath();
            } catch (IOException e) {
                throw new Error(e);
            }
        };

        return createPathsWithFunction(count, function);
    }

    public Collection<Path> createDirectories(String baseName, int count) throws IOException {
        final TempPathGenerator pathGenerator = new TempPathGenerator(mDirectory, baseName, "");

        IntFunction<Path> function = i -> {
            try {
                return pathGenerator.generateDirectory().originalPath();
            } catch (IOException e) {
                throw new Error(e);
            }
        };

        return createPathsWithFunction(count, function);
    }

    public Collection<Path> createPathsWithFunction(int count, IntFunction<Path> pathFunction) throws IOException {
        Collection<Path> paths = IntStream.range(0, count)
                .mapToObj(pathFunction)
                .collect(Collectors.toList());

        if (paths.size() != count) {
            throw new IOException("Unable to create all paths");
        }

        return paths;
    }

    private String generateName(String baseName, int index) {
        return String.format("%d%s%d",
                mRandom.nextLong(),
                baseName,
                index);
    }
}
