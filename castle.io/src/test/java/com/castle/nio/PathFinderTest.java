package com.castle.nio;

import com.castle.nio.temp.TempPathGenerator;
import com.castle.testutil.io.RandomPathGenerator;
import com.castle.testutil.io.TemporaryPaths;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class PathFinderTest {

    @TempDir
    public Path mTempDirRoot;

    private Path mTemporaryFolder;
    private RandomPathGenerator mRandomPathGenerator;

    private Path mTemporaryFolder2;
    private RandomPathGenerator mRandomPathGenerator2;

    private FileSystem mFileSystem;
    private Path mRoot;

    @BeforeEach
    public void setup() throws Exception {
        TempPathGenerator pathGenerator = TemporaryPaths.pathGenerator(mTempDirRoot);
        mTemporaryFolder = pathGenerator.generateDirectory().originalPath();
        mTemporaryFolder2 = pathGenerator.generateDirectory().originalPath();

        mRandomPathGenerator = new RandomPathGenerator(mTemporaryFolder);
        mRandomPathGenerator2 = new RandomPathGenerator(mTemporaryFolder2);

        mFileSystem = mTemporaryFolder.getFileSystem();
        mRoot = mTemporaryFolder;
    }

    @Test
    public void findAll_allMatchingFiles_findsAll() throws Exception {
        final int FILE_COUNT = 10;
        final String BASE_NAME = "base";

        Collection<Path> paths = mRandomPathGenerator.createFiles(BASE_NAME, FILE_COUNT);

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

        Collection<Path> paths = mRandomPathGenerator.createFiles(BASE_NAME, FILE_COUNT);
        mRandomPathGenerator.createFiles("notmatch", 30);

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

        Collection<Path> paths = mRandomPathGenerator.createFiles(BASE_NAME, FILE_COUNT);
        mRandomPathGenerator.createDirectories("d" + BASE_NAME, 10);

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
        paths.addAll(mRandomPathGenerator.createFiles(BASE_NAME, FILE_COUNT));
        paths.addAll(mRandomPathGenerator2.createFiles(BASE_NAME, FILE_COUNT));

        PathFinder pathFinder = new PathFinder(mFileSystem);

        PathMatcher pathMatcher = mFileSystem.getPathMatcher(String.format("regex:%s",
                String.format("%s/.*/.*%s.*", mRoot.getParent().toAbsolutePath().toString(),
                        BASE_NAME)));

        Collection<Path> foundPaths = pathFinder.findAll(
                PathMatching.pathMatcher(pathMatcher),
                Arrays.asList(mTemporaryFolder, mTemporaryFolder2));

        assertThat(foundPaths, containsInAnyOrder(paths.toArray()));
    }

    private String createPathRegex(String nameRegex) {
        return String.format("%s/%s",
                mRoot.toAbsolutePath().toString(),
                nameRegex);
    }
}