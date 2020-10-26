package com.castle.reflect;

import com.castle.reflect.exceptions.TypeException;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class NumberAdapter implements TypeAdapter<Number> {

    private final Map<Class<?>, Function<Number, ?>> mNumberConverters;

    public NumberAdapter() {
        mNumberConverters = new HashMap<>();
        mNumberConverters.put(Byte.class, Number::byteValue);
        mNumberConverters.put(Short.class, Number::shortValue);
        mNumberConverters.put(Integer.class, Number::intValue);
        mNumberConverters.put(Long.class, Number::longValue);
        mNumberConverters.put(Float.class, Number::floatValue);
        mNumberConverters.put(Double.class, Number::doubleValue);
    }

    @Override
    public <R> boolean canAdapt(Class<R> type) {
        Class<?> actualType = type.isPrimitive() ? Types.toWrapperClass(type) : type;
        return mNumberConverters.containsKey(actualType);
    }

    @Override
    public <R> R adapt(Number number, Class<R> type) {
        Class<?> actualType = type.isPrimitive() ? Types.toWrapperClass(type) : type;
        Function<Number, ?> converter = mNumberConverters.get(actualType);
        if (converter == null) {
            throw new TypeException("Unsupported type: " + type);
        }
        if (number == null) {
            return null;
        }

        return type.cast(converter.apply(number));
    }
}
