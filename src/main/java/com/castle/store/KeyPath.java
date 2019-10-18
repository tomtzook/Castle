package com.castle.store;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;

public class KeyPath implements Iterable<String> {

    public static final KeyPath ROOT = new KeyPath("");
    private static final String SEPARATOR = ".";

    private final String mPath;

    private KeyPath(String path) {
        mPath = path;
    }

    public static KeyPath of(String first, String... more) {
        return new KeyPath(buildPath(first, more));
    }

    public static KeyPath of(KeyPath parent, String... more) {
        return of(parent.mPath, more);
    }

    public String getPath() {
        return mPath;
    }

    public String getName() {
        int lastSeparator = mPath.lastIndexOf(SEPARATOR);
        if (lastSeparator < 0) {
            return mPath;
        }

        return mPath.substring(lastSeparator + 1);
    }

    public KeyPath getParent() {
        if (isRoot()) {
            throw new IllegalStateException("root has no parent");
        }

        int lastSeparator = mPath.lastIndexOf(SEPARATOR);
        if (lastSeparator < 0) {
            return ROOT;
        }

        return new KeyPath(mPath.substring(0, lastSeparator));
    }

    public boolean isRoot() {
        return mPath.isEmpty();
    }

    @Override
    public Iterator<String> iterator() {
        Deque<String> parts = new ArrayDeque<>();
        KeyPath current = this;
        while (!current.isRoot()) {
            parts.addFirst(current.getName());
            current = current.getParent();
        }

        return parts.iterator();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof KeyPath) {
            return mPath.equals(((KeyPath)obj).mPath);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return mPath.hashCode();
    }

    @Override
    public String toString() {
        return mPath;
    }

    private static String buildPath(String first, String... more) {
        StringBuilder builder = new StringBuilder(first);
        for (String part : more) {
            builder.append(SEPARATOR);
            builder.append(part);
        }

        return builder.toString();
    }
}
