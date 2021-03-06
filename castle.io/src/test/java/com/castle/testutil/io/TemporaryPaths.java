package com.castle.testutil.io;

import com.castle.nio.PathFinder;
import com.castle.nio.PathMatching;
import com.castle.nio.PatternPathFinder;
import com.castle.nio.temp.TempPathGenerator;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class TemporaryPaths {

    public static TempPathGenerator pathGenerator(TemporaryFolder temporaryFolder, String prefix, String suffix) {
        return new TempPathGenerator(temporaryFolder.getRoot().toPath(), "", "");
    }

    public static TempPathGenerator pathGenerator(TemporaryFolder temporaryFolder) {
        return pathGenerator(temporaryFolder, "", "");
    }

    public static TempPathGenerator pathGenerator(Path path, String prefix, String suffix) {
        return new TempPathGenerator(path, "", "");
    }

    public static TempPathGenerator pathGenerator(Path path) {
        return pathGenerator(path, "", "");
    }

    public static PatternPathFinder pathFinder(TemporaryFolder temporaryFolder) {
        return new PatternPathFinder(temporaryFolder.getRoot().toPath().getFileSystem());
    }

    public static PatternPathFinder pathFinder(Path path) {
        return new PatternPathFinder(path.getFileSystem());
    }

    public static Matcher<Path> pathNotExists() {
        return new TypeSafeMatcher<Path>(Path.class) {

            @Override
            protected boolean matchesSafely(Path item) {
                return !Files.exists(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Path not exists");
            }
        };
    }

    public static Matcher<String> doesAPathEndWithString(Path rootPath) {
        return new TypeSafeMatcher<String>(String.class) {

            private final Path mRootPath = rootPath;
            private final FileSystem mFileSystem = mRootPath.getFileSystem();
            private final PathFinder mPathFinder = new PathFinder(mFileSystem);

            @Override
            public boolean matchesSafely(String value) {
                PathMatcher pathMatcher = mFileSystem.getPathMatcher(String.format("regex:.*%s$", value));
                try {
                    return !mPathFinder.findAll(PathMatching.fileMatcher(pathMatcher), mRootPath).isEmpty();
                } catch (IOException e) {
                    throw new Error(e);
                }
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("Find path that ends with string under: ");
                description.appendValue(mRootPath);
            }
        };
    }
}
