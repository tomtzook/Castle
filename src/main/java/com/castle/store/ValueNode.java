package com.castle.store;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ValueNode {

    private final Object mValue;
    private final Map<String, ValueNode> mChildren;

    private ValueNode(Object value, Map<String, ValueNode> children) {
        mValue = value;
        mChildren = children;
    }

    public ValueNode(Object value) {
        this(value, new HashMap<>());
    }

    public ValueNode() {
        this(null);
    }

    public Object rawValue() {
        return mValue;
    }

    public <T> T valueAsType(Class<T> type) {
        return type.cast(mValue);
    }

    public boolean hasValue() {
        return mValue != null;
    }

    public Map<String, ValueNode> getChildren() {
        return Collections.unmodifiableMap(mChildren);
    }

    public ValueNode getChild(String name) {
        return mChildren.get(name);
    }

    public boolean hasChild(String name) {
        return mChildren.containsKey(name);
    }

    public void putChild(String name, ValueNode child) {
        mChildren.put(name, child);
    }

    @Override
    public String toString() {
        return String.format("%s->%s", String.valueOf(mValue), mChildren);
    }
}
