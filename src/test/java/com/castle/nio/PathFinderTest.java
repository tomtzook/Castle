package com.castle.nio;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.Assert.assertThat;

public class PathFinderTest {

    @Rule
    public TemporaryFolder mTemporaryFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder mTemporaryFolder2 = new TemporaryFolder();

    private FileSystem mFileSystem;
    private Path mRoot;

    @Before
    public void setup() throws Exception {
        mFileSystem = mTemporaryFolder.getRoot().toPath().getFileSystem();
        mRoot = mTemporaryFolder.getRoot().toPath();
    }

    @Test
    public void findAll_allMatchingFiles_findsAll() throws Exception {
        final int FILE_COUNT = 10;
        final String BASE_NAME = "base";

        Collection<Path> paths = createFiles(BASE_NAME, FILE_COUNT);

        PathFinder pathFinder = new PathFinder(mFileSystem);

        PathMatcher pathMatcher = mFileSystem.getPathMatcher(String.format("regex:%s",
                createPathRegex(".*" + BASE_NAME + ".*")));
        Collection<Path> foundPaths = pathFinder.findAll(PathMatching.pathMatcher(pathMatcher), mRoot);

        assertThat(foundPaths, containsInAnyOrder(paths.toArray()));
    }

    @Test
    public void findAll_severalMatchingFiles_findsMatching() throws Exception {
        final int FILE_COUNT = 10;
        final String BASE_NAME = "base";

        Collection<Path> paths = createFiles(BASE_NAME, FILE_COUNT);
        createFiles("notmatch", 30);

        PathFinder pathFinder = new PathFinder(mFileSystem);

        PathMatcher pathMatcher = mFileSystem.getPathMatcher(String.format("regex:%s",
                createPathRegex(".*" + BASE_NAME + ".*")));
        Collection<Path> foundPaths = pathFinder.findAll(PathMatching.pathMatcher(pathMatcher), mRoot);

        assertThat(foundPaths, containsInAnyOrder(paths.toArray()));
    }

    @Test
    public void findAll_severalAreFilesSeveralAreNot_findsOnlyTheFiles() throws Exception {
        final int FILE_COUNT = 10;
        final String BASE_NAME = "base";

        Collection<Path> paths = createFiles(BASE_NAME, FILE_COUNT);
        createDirectories("d" + BASE_NAME, 10);

        PathFinder pathFinder = new PathFinder(mFileSystem);

        PathMatcher pathMatcher = mFileSystem.getPathMatcher(String.format("regex:%s",
                createPathRegex(".*" + BASE_NAME + ".*")));

        Collection<Path> foundPaths = pathFinder.findAll(PathMatching.fileMatcher(pathMatcher), mRoot);

        assertThat(foundPaths, containsInAnyOrder(paths.toArray()));
    }

    @Test
    public void findAll_multipleRoots_findsFromAllRoots() throws IOException {
        final int FILE_COUNT = 10;
        final String BASE_NAME = "base";

        Collection<Path> paths = new ArrayList<>();
        paths.addAll(createFiles(mTemporaryFolder, BASE_NAME, FILE_COUNT));
        paths.addAll(createFiles(mTemporaryFolder2, BASE_NAME, FILE_COUNT));

        PathFinder pathFinder = new PathFinder(mFileSystem);

        PathMatcher pathMatcher = mFileSystem.getPathMatcher(String.format("regex:%s",
                String.format("%s/.*/.*%s.*", mRoot.getParent().toAbsolutePath().toString(),
                        BASE_NAME)));

        Collection<Path> foundPaths = pathFinder.findAll(
                PathMatching.pathMatcher(pathMatcher),
                Arrays.asList(mTemporaryFolder.getRoot().toPath(), mTemporaryFolder2.getRoot().toPath()));

        assertThat(foundPaths, containsInAnyOrder(paths.toArray()));
    }

    private String createPathRegex(String nameRegex) {
        return String.format("%s/%s",
                mRoot.toAbsolutePath().toString(),
                nameRegex);
    }

    private Collection<Path> createFiles(String baseName, int count) throws IOException {
        return createFiles(mTemporaryFolder, baseName, count);
    }

    private Collection<Path> createFiles(TemporaryFolder temporaryFolder, String baseName, int count) throws IOException {
        IntFunction<Path> function = i -> {
            try {
                return temporaryFolder.newFile(String.format("%s%d", baseName, i)).toPath();
            } catch (IOException e) {
                return null;
            }
        };

        return createPathsWithFunction(count, function);
    }

    private Collection<Path> createDirectories(String baseName, int count) throws IOException {
        IntFunction<Path> function = i -> {
            try {
                return mTemporaryFolder.newFolder(String.format("%s%d", baseName, i)).toPath();
            } catch (IOException e) {
                return null;
            }
        };

        return createPathsWithFunction(count, function);
    }

    private Collection<Path> createPathsWithFunction(int count, IntFunction<Path> pathFunction) throws IOException {
        Collection<Path> paths = IntStream.range(0, count)
                .mapToObj(pathFunction)
                .collect(Collectors.toList());

        if (paths.size() != count) {
            throw new IOException("Unable to create all paths");
        }

        return paths;
    }
}