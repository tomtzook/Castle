package com.castle.reflect;

import com.castle.reflect.exceptions.TypeException;

public interface DataType {

    boolean isInstance(Object value);
    boolean canConvertTo(Class<?> type);
    <T> T convertTo(Object value, Class<T> type);

    class Primitive implements DataType {

        private final Class<?> mType;

        public Primitive(Class<?> type) {
            mType = type;
            assert type.isPrimitive();
        }

        @Override
        public boolean isInstance(Object value) {
            return mType.isInstance(value);
        }

        @Override
        public boolean canConvertTo(Class<?> type) {
            return mType.equals(type) || type.isAssignableFrom(mType);
        }

        @Override
        public <T> T convertTo(Object value, Class<T> type) {
            if (!isInstance(value)) {
                throw new IllegalArgumentException("value is not of this DataType");
            }
            if (!canConvertTo(type)) {
                throw new TypeException("type is not assignable from this DataType");
            }

            return type.cast(value);
        }
    }

    class Numeric implements DataType {

        private final NumberAdapter mAdapter = new NumberAdapter();

        @Override
        public boolean isInstance(Object value) {
            return value instanceof Number;
        }

        @Override
        public boolean canConvertTo(Class<?> type) {
            return mAdapter.canAdapt(type);
        }

        @Override
        public <T> T convertTo(Object value, Class<T> type) {
            if (!isInstance(value)) {
                throw new IllegalArgumentException("value is not of this DataType");
            }
            if (!canConvertTo(type)) {
                throw new TypeException("type is not assignable from this DataType");
            }

            return mAdapter.adapt((Number) value, type);
        }
    }
}
