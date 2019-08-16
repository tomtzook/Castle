package com.castle.testutil.io;

import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class RandomPathGenerator {

    private final TemporaryFolder mTemporaryFolder;

    public RandomPathGenerator(TemporaryFolder temporaryFolder) {
        mTemporaryFolder = temporaryFolder;
    }

    public Collection<Path> createFiles(String baseName, int count) throws IOException {
        IntFunction<Path> function = i -> {
            try {
                return mTemporaryFolder.newFile(String.format("%s%d", baseName, i)).toPath();
            } catch (IOException e) {
                throw new Error(e);
            }
        };

        return createPathsWithFunction(count, function);
    }

    public Collection<Path> createDirectories(String baseName, int count) throws IOException {
        IntFunction<Path> function = i -> {
            try {
                return mTemporaryFolder.newFolder(String.format("%s%d", baseName, i)).toPath();
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
}
