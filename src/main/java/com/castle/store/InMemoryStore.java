package com.castle.store;

import com.castle.store.exceptions.KeyNotFoundException;
import com.castle.store.exceptions.StoreException;

import java.util.Iterator;
import java.util.Map;

public class InMemoryStore implements Store {

    private final Map<String, ValueNode> mNodeMap;

    public InMemoryStore(Map<String, ValueNode> nodeMap) {
        mNodeMap = nodeMap;
    }

    @Override
    public void store(KeyPath key, Object value) throws StoreException {
        storeTree(key, new ValueNode(value));
    }

    @Override
    public void storeTree(KeyPath key, ValueNode root) throws StoreException {
        Iterator<String> parts = key.iterator();
        if (!parts.hasNext()) {
            throw new IllegalArgumentException("cannot store in root path");
        }

        String first = parts.next();
        if (!parts.hasNext()) {
            mNodeMap.put(first, root);
            return;
        }

        ValueNode lastRoot = getOrCreateRootNode(first);
        while (parts.hasNext()) {
            String current = parts.next();

            if (!parts.hasNext()) {
                lastRoot.putChild(current, root);
                return;
            }

            lastRoot = getOrCreateNode(current, lastRoot);
        }
    }

    @Override
    public <T> T retrieve(KeyPath key, Class<T> valueType) throws StoreException, KeyNotFoundException {
        return retrieveTree(key).valueAsType(valueType);
    }

    @Override
    public ValueNode retrieveTree(KeyPath key) throws StoreException, KeyNotFoundException {
        Iterator<String> parts = key.iterator();
        if (!parts.hasNext()) {
            throw new IllegalArgumentException("cannot retrieve from root path");
        }

        String first = parts.next();
        ValueNode lastRoot = getRootNode(key, first);

        while (parts.hasNext()) {
            String current = parts.next();

            lastRoot = getNode(key, current, lastRoot);
        }

        return lastRoot;
    }

    private ValueNode getOrCreateRootNode(String key) {
        ValueNode node = mNodeMap.get(key);
        if (node == null) {
            node = new ValueNode();
            mNodeMap.put(key, node);
        }

        return node;
    }

    private ValueNode getOrCreateNode(String key, ValueNode nodeParent) {
        ValueNode node = nodeParent.getChild(key);
        if (node == null) {
            node = new ValueNode();
            nodeParent.putChild(key, node);
        }

        return node;
    }

    private ValueNode getRootNode(KeyPath fullKey, String key) throws KeyNotFoundException {
        if (mNodeMap.containsKey(key)) {
            return mNodeMap.get(key);
        }

        throw new KeyNotFoundException(fullKey);
    }

    private ValueNode getNode(KeyPath fullKey, String key, ValueNode baseNode) throws KeyNotFoundException {
        if (baseNode.hasChild(key)) {
            return baseNode.getChild(key);
        }

        throw new KeyNotFoundException(fullKey);
    }
}
