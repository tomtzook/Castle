package com.castle.reflect;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class Types {

    private Types() {}

    private static final Map<Class<?>, Function<Number, ?>> sNumberConverters;

    static {
        sNumberConverters = new HashMap<>();
        sNumberConverters.put(Byte.class, Number::byteValue);
        sNumberConverters.put(Short.class, Number::shortValue);
        sNumberConverters.put(Integer.class, Number::intValue);
        sNumberConverters.put(Long.class, Number::longValue);
        sNumberConverters.put(Float.class, Number::floatValue);
        sNumberConverters.put(Double.class, Number::doubleValue);
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
