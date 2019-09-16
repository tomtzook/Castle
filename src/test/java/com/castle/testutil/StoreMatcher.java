package com.castle.testutil;

import com.castle.store.KeyPath;
import com.castle.store.ValueNode;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class StoreMatcher {

    private StoreMatcher() {}

    public static Matcher<Map<String, ValueNode>> mapContainsNodeWithValue(KeyPath keyPath, Object value) {
        return new TypeSafeMatcher<Map<String, ValueNode>>() {

            @Override
            protected boolean matchesSafely(Map<String, ValueNode> map) {
                ValueNode currentNode = findNode(map, keyPath);
                if (currentNode == null) {
                    return false;
                }

                return Objects.equals(currentNode.rawValue(), value);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(keyPath);
                description.appendText("=");
                description.appendValue(value);
            }
        };
    }

    public static Matcher<Map<String, ValueNode>> mapContainsNode(KeyPath keyPath, ValueNode node) {
        return new TypeSafeMatcher<Map<String, ValueNode>>() {

            @Override
            protected boolean matchesSafely(Map<String, ValueNode> map) {
                ValueNode currentNode = findNode(map, keyPath);
                if (currentNode == null) {
                    return false;
                }

                return Objects.equals(currentNode, node);
            }

            @Override
            public void describeTo(Description description) {
                description.appendValue(keyPath);
                description.appendText("=");
                description.appendValue(node);
            }
        };
    }

    private static ValueNode findNode(Map<String, ValueNode> map, KeyPath keyPath) {
        Iterator<String> iterator = keyPath.iterator();

        String rawKey = iterator.next();
        ValueNode currentNode = map.get(rawKey);
        if (currentNode == null) {
            return null;
        }

        while (iterator.hasNext()) {
            rawKey = iterator.next();
            if (!currentNode.hasChild(rawKey)) {
                return null;
            }

            currentNode = currentNode.getChild(rawKey);
        }

        return currentNode;
    }
}
