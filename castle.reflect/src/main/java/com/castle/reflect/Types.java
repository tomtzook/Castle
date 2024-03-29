package com.castle.reflect;

import com.castle.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;

@Immutable
public class Types {

    private Types() {
    }

    private static final Map<Class<?>, Class<?>> sPrimitivesToWrappers;

    static {
        sPrimitivesToWrappers = new HashMap<>();
        sPrimitivesToWrappers.put(boolean.class, Boolean.class);
        sPrimitivesToWrappers.put(char.class, Character.class);
        sPrimitivesToWrappers.put(byte.class, Byte.class);
        sPrimitivesToWrappers.put(short.class, Short.class);
        sPrimitivesToWrappers.put(int.class, Integer.class);
        sPrimitivesToWrappers.put(long.class, Long.class);
        sPrimitivesToWrappers.put(float.class, Float.class);
        sPrimitivesToWrappers.put(double.class, Double.class);
    }

    public static Class<?> toWrapperClass(Class<?> primitiveType) {
        if (!primitiveType.isPrimitive()) {
            throw new IllegalArgumentException("Not primitive " + primitiveType.getSimpleName());
        }
        if (!sPrimitivesToWrappers.containsKey(primitiveType)) {
            throw new AssertionError("No wrapper for primitive");
        }

        return sPrimitivesToWrappers.get(primitiveType);
    }
}
