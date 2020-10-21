package com.castle.reflect;

import com.castle.annotations.Immutable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Immutable
public class Types {

    private Types() {
    }

    private static final Map<Class<?>, Class<?>> sPrimitivesToWrappers;
    private static final Map<Class<?>, Function<Number, ?>> sNumberConverters;

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

        sNumberConverters = new HashMap<>();
        sNumberConverters.put(Byte.class, Number::byteValue);
        sNumberConverters.put(Short.class, Number::shortValue);
        sNumberConverters.put(Integer.class, Number::intValue);
        sNumberConverters.put(Long.class, Number::longValue);
        sNumberConverters.put(Float.class, Number::floatValue);
        sNumberConverters.put(Double.class, Number::doubleValue);
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

    public static <T> T smartCast(Object value, Class<T> type) {
        if (type.isAssignableFrom(value.getClass())) {
            return type.cast(value);
        }

        if (value instanceof Number && type.getSuperclass().equals(Number.class)) {
            Function<Number, ?> converter = sNumberConverters.get(type);
            if (converter != null) {
                return type.cast(converter.apply((Number) value));
            }
        }

        throw new AssertionError("Can't cast " + value.getClass() + "to " + type);
    }
}
