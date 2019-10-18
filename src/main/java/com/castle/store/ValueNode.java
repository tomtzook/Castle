package com.castle.store;

import com.castle.reflect.exceptions.TypeMismatchException;

import javax.lang.model.type.NullType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ValueNode {

    private final Object mValue;
    private final Map<String, ValueNode> mChildren;

    ValueNode(Object value, Map<String, ValueNode> children) {
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
        try {
            return type.cast(mValue);
        } catch (ClassCastException e) {
            throw new TypeMismatchException(type, hasValue() ? mValue.getClass() : null);
        }
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

    public boolean equals(ValueNode other) {
        if (!Objects.equals(mValue, other.mValue)) {
            return false;
        }

        return mChildren.equals(other.mChildren);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ValueNode && equals((ValueNode) obj);
    }
}
