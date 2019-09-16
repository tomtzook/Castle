package com.castle.store;

import com.castle.testutil.StoreMatcher;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

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

    private void putByPath(Map<String, ValueNode> map, KeyPath keyPath, Object value) {
        ValueNode node = new ValueNode(value);

        while (!keyPath.getParent().isRoot()) {
            ValueNode parent = new ValueNode();
            parent.putChild(keyPath.getName(), node);
            node = parent;

            keyPath = keyPath.getParent();
        }

        map.put(keyPath.getName(), node);
    }
}