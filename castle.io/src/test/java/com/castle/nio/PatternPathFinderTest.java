package com.castle.nio;

import com.castle.testutil.io.RandomPathGenerator;
import com.castle.util.regex.Patterns;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class PatternPathFinderTest {

    @TempDir
    public Path mTemporaryFolder;
    private RandomPathGenerator mRandomPathGenerator;

    private FileSystem mFileSystem;
    private Path mRoot;

    @BeforeEach
    public void setup() {
        mRandomPathGenerator = new RandomPathGenerator(mTemporaryFolder);
        mFileSystem = mTemporaryFolder.getFileSystem();
        mRoot = mTemporaryFolder;
    }

    @Test
    public void find_withPatternForFiles_findsAllFiles() throws Exception {
        final String BASE_PATTERN = "patternThatIsUnique";
        final String BASE_NAME = String.format("sd%s123", BASE_PATTERN);
        final String BASE_NAME2 = String.format("%sdg2", BASE_PATTERN);

        final Collection<Path> MATCHING_PATHS = new ArrayList<>();
        MATCHING_PATHS.addAll(mRandomPathGenerator.createFiles(BASE_NAME, 10));
        MATCHING_PATHS.addAll(mRandomPathGenerator.createFiles(BASE_NAME2, 10));

        mRandomPathGenerator.createFiles("nonm", 5);
        mRandomPathGenerator.createFiles("matching", 3);
        mRandomPathGenerator.createFiles("pattern", 2);
        mRandomPathGenerator.createDirectories(BASE_NAME, 5);

        PatternPathFinder patternPathFinder = new PatternPathFinder(mFileSystem);
        Collection<Path> foundPaths = patternPathFinder.findAll(
                Patterns.wrapWithWildcards(BASE_PATTERN),
                PathMatching.fileMatcher(),
                mRoot);

        assertThat(foundPaths, containsInAnyOrder(MATCHING_PATHS.toArray()));
    }

    @Test
    public void find_withPatternForDirectories_findsAllDirectories() throws Exception {
        final String BASE_PATTERN = "patternThatIsUnique";
        final String BASE_NAME = String.format("sd%s123", BASE_PATTERN);
        final String BASE_NAME2 = String.format("%sdg2", BASE_PATTERN);

        final Collection<Path> MATCHING_PATHS = new ArrayList<>();
        MATCHING_PATHS.addAll(mRandomPathGenerator.createDirectories(BASE_NAME, 10));
        MATCHING_PATHS.addAll(mRandomPathGenerator.createDirectories(BASE_NAME2, 10));

        mRandomPathGenerator.createDirectories("nonm", 5);
        mRandomPathGenerator.createDirectories("matching", 3);
        mRandomPathGenerator.createDirectories("pattern", 2);
        mRandomPathGenerator.createFiles(BASE_NAME, 5);

        PatternPathFinder patternPathFinder = new PatternPathFinder(mFileSystem);
        Collection<Path> foundPaths = patternPathFinder.findAll(
                Patterns.wrapWithWildcards(BASE_PATTERN),
                PathMatching.directoryMatcher(),
                mRoot);

        assertThat(foundPaths, containsInAnyOrder(MATCHING_PATHS.toArray()));
    }

    @Test
    public void find_withPattern_findsAll() throws Exception {
        final String BASE_PATTERN = "patternThatIsUnique";
        final String BASE_NAME = String.format("sd%s123", BASE_PATTERN);
        final String BASE_NAME2 = String.format("%sdg2", BASE_PATTERN);

        final Collection<Path> MATCHING_PATHS = new ArrayList<>();
        MATCHING_PATHS.addAll(mRandomPathGenerator.createDirectories(BASE_NAME, 4));
        MATCHING_PATHS.addAll(mRandomPathGenerator.createFiles(BASE_NAME, 4));
        MATCHING_PATHS.addAll(mRandomPathGenerator.createDirectories(BASE_NAME2, 3));
        MATCHING_PATHS.addAll(mRandomPathGenerator.createFiles(BASE_NAME2, 3));

        mRandomPathGenerator.createDirectories("nonm", 5);
        mRandomPathGenerator.createFiles("nonm", 5);
        mRandomPathGenerator.createDirectories("matching", 3);
        mRandomPathGenerator.createFiles("matching", 3);
        mRandomPathGenerator.createDirectories("pattern", 2);
        mRandomPathGenerator.createFiles("pattern", 2);

        PatternPathFinder patternPathFinder = new PatternPathFinder(mFileSystem);
        Collection<Path> foundPaths = patternPathFinder.findAll(
                Patterns.wrapWithWildcards(BASE_PATTERN),
                PathMatching.allMatcher(),
                mRoot);

        assertThat(foundPaths, containsInAnyOrder(MATCHING_PATHS.toArray()));
    }
}