package com.castle.store;

import com.castle.reflect.exceptions.TypeMismatchException;
import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.testutil.StoreMatcher;
import org.hamcrest.Matchers;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

public class InMemoryStoreTest {

    @Test
    public void store_nodeNotInMap_insertsNewNode() throws Exception {
        final KeyPath KEY = KeyPath.of("first");
        final Object VALUE = new Object();

        Map<String, ValueNode> map = new HashMap<>();
        InMemoryStore store = new InMemoryStore(map);

        store.store(KEY, VALUE);

        assertThat(map, StoreMatcher.mapContainsNodeWithValue(KEY, VALUE));
    }

    @Test
    public void store_nodeInMap_overridesNodeValue() throws Exception {
        final KeyPath KEY = KeyPath.of("first");
        final String MAP_KEY = KEY.iterator().next();
        final Object VALUE = new Object();

        Map<String, ValueNode> map = new HashMap<>();
        map.put(MAP_KEY, new ValueNode(new Object()));

        InMemoryStore store = new InMemoryStore(map);

        store.store(KEY, VALUE);

        assertThat(map, StoreMatcher.mapContainsNodeWithValue(KEY, VALUE));
    }

    @Test
    public void store_inNewTree_createsNodesOnTheWay() throws Exception {
        final KeyPath KEY = KeyPath.of("first", "second", "third");
        final Object VALUE = new Object();

        Map<String, ValueNode> map = new HashMap<>();

        InMemoryStore store = new InMemoryStore(map);

        store.store(KEY, VALUE);

        assertThat(map, StoreMatcher.mapContainsNodeWithValue(KEY, VALUE));
    }

    @Test
    public void store_inAnExistingTree_overridesValueOfNode() throws Exception {
        final KeyPath KEY = KeyPath.of("first", "second", "third");
        final Object VALUE = new Object();

        Map<String, ValueNode> map = new HashMap<>();
        putByPath(map, KEY, new Object());

        InMemoryStore store = new InMemoryStore(map);

        store.store(KEY, VALUE);

        assertThat(map, StoreMatcher.mapContainsNodeWithValue(KEY, VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void store_rootPath_throwsIllegalArgumentException() throws Exception {
        final KeyPath KEY = KeyPath.ROOT;
        final Object VALUE = new Object();

        Map<String, ValueNode> map = new HashMap<>();
        InMemoryStore store = new InMemoryStore(map);

        store.store(KEY, VALUE);
    }

    @Test
    public void storeTree_nodeNotInMap_insertsNewNode() throws Exception {
        final KeyPath KEY = KeyPath.of("first");
        final ValueNode NODE = new ValueNode();

        Map<String, ValueNode> map = new HashMap<>();
        InMemoryStore store = new InMemoryStore(map);

        store.storeTree(KEY, NODE);

        assertThat(map, StoreMatcher.mapContainsNode(KEY, NODE));
    }

    @Test
    public void storeTree_nodeInMap_overridesNode() throws Exception {
        final KeyPath KEY = KeyPath.of("first");
        final String MAP_KEY = KEY.iterator().next();
        final ValueNode NODE = new ValueNode();

        Map<String, ValueNode> map = new HashMap<>();
        map.put(MAP_KEY, new ValueNode(new Object()));

        InMemoryStore store = new InMemoryStore(map);

        store.storeTree(KEY, NODE);

        assertThat(map, StoreMatcher.mapContainsNode(KEY, NODE));
    }

    @Test
    public void storeTree_inNewTree_createsNodesOnTheWay() throws Exception {
        final KeyPath KEY = KeyPath.of("first", "second", "third");
        final ValueNode NODE = new ValueNode();

        Map<String, ValueNode> map = new HashMap<>();

        InMemoryStore store = new InMemoryStore(map);

        store.storeTree(KEY, NODE);

        assertThat(map, StoreMatcher.mapContainsNode(KEY, NODE));
    }

    @Test
    public void storeTree_inAnExistingTree_overridesNode() throws Exception {
        final KeyPath KEY = KeyPath.of("first", "second", "third");
        final ValueNode NODE = new ValueNode();

        Map<String, ValueNode> map = new HashMap<>();
        putByPath(map, KEY, new Object());

        InMemoryStore store = new InMemoryStore(map);

        store.storeTree(KEY, NODE);

        assertThat(map, StoreMatcher.mapContainsNode(KEY, NODE));
    }

    @Test
    public void storeTree_pathIsRoot_placesDirectlyIntoMap() throws Exception {
        final KeyPath KEY = KeyPath.ROOT;

        final ValueNode VALUE_NODE = new ValueNode();
        VALUE_NODE.putChild("key1", new ValueNode("value2"));
        VALUE_NODE.putChild("keeas", new ValueNode(2));


        Map<String, ValueNode> map = new HashMap<>();
        InMemoryStore store = new InMemoryStore(map);

        store.storeTree(KEY, VALUE_NODE);

        assertThat(map, equalTo(VALUE_NODE.getChildren()));
    }

    @Test
    public void retrieve_nodeInMap_retrievesValue() throws Exception {
        final KeyPath KEY = KeyPath.of("first");
        final Object VALUE = new Object();

        Map<String, ValueNode> map = new HashMap<>();
        putByPath(map, KEY, VALUE);

        InMemoryStore store = new InMemoryStore(map);

        Object value = store.retrieve(KEY, Object.class);

        assertThat(value, equalTo(VALUE));
    }

    @Test(expected = IllegalArgumentException.class)
    public void retrieve_fromRoot_throwsIllegalArgumentException() throws Exception {
        final KeyPath KEY = KeyPath.ROOT;

        Map<String, ValueNode> map = new HashMap<>();
        InMemoryStore store = new InMemoryStore(map);

        store.retrieve(KEY, Object.class);
    }

    @Test(expected = TypeMismatchException.class)
    public void retrieve_nodeInMapButWrongType_throwsTypeMismatchException() throws Exception {
        final KeyPath KEY = KeyPath.of("first");
        final String VALUE = "adase";

        Map<String, ValueNode> map = new HashMap<>();
        putByPath(map, KEY, VALUE);

        InMemoryStore store = new InMemoryStore(map);

        store.retrieve(KEY, Integer.class);
    }

    @Test(expected = KeyNotFoundException.class)
    public void retrieve_keyMissing_throwsKeyNotFoundException() throws Exception {
        final KeyPath KEY = KeyPath.of("first", "second", "third");
        final ValueNode PARENT_NODE = new ValueNode("asd");

        Map<String, ValueNode> map = new HashMap<>();
        putByPath(map, KEY.getParent(), PARENT_NODE);

        InMemoryStore store = new InMemoryStore(map);

        store.retrieve(KEY, Integer.class);
    }

    @Test
    public void retrieveTree_treeFromRoot_returnsTreeWithCorrectValues() throws Exception {
        final KeyPath KEY = KeyPath.of("first");
        final ValueNode NODE = new ValueNode("value");
        NODE.putChild("second", new ValueNode("map"));

        Map<String, ValueNode> map = new HashMap<>();
        putByPath(map, KEY, NODE);

        InMemoryStore store = new InMemoryStore(map);

        ValueNode node = store.retrieveTree(KEY);

        assertThat(node, equalTo(NODE));
    }

    @Test
    public void retrieveTree_treeNotFromRoot_returnsTreeWithCorrectValues() throws Exception {
        final KeyPath KEY = KeyPath.of("first", "anotherMiddle");
        final ValueNode NODE = new ValueNode("value");
        NODE.putChild("second", new ValueNode("map"));

        Map<String, ValueNode> map = new HashMap<>();
        putByPath(map, KEY, NODE);

        InMemoryStore store = new InMemoryStore(map);

        ValueNode node = store.retrieveTree(KEY);

        assertThat(node, equalTo(NODE));
    }

    @Test(expected = KeyNotFoundException.class)
    public void retrieveTree_keyMissing_throwsKeyNotFoundException() throws Exception {
        final KeyPath KEY = KeyPath.of("first");

        Map<String, ValueNode> map = new HashMap<>();
        InMemoryStore store = new InMemoryStore(map);

        store.retrieveTree(KEY);
    }

    private void putByPath(Map<String, ValueNode> map, KeyPath keyPath, Object value) {
        putByPath(map, keyPath, new ValueNode(value));
    }

    private void putByPath(Map<String, ValueNode> map, KeyPath keyPath, ValueNode node) {
        while (!keyPath.getParent().isRoot()) {
            ValueNode parent = new ValueNode();
            parent.putChild(keyPath.getName(), node);
            node = parent;

            keyPath = keyPath.getParent();
        }

        map.put(keyPath.getName(), node);
    }
}