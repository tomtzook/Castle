package com.castle.reflect;

public interface Value {

    Object rawValue();

    boolean canConvertTo(Class<?> type);
    <T> T convertTo(Class<T> type);

    class Impl implements Value {

        private final Object mValue;
        private final DataType mType;

        public Impl(Object value, DataType type) {
            mValue = value;
            mType = type;

            assert mType.isInstance(mValue);
        }

        @Override
        public Object rawValue() {
            return mValue;
        }

        @Override
        public boolean canConvertTo(Class<?> type) {
            return mType.canConvertTo(type);
        }

        @Override
        public <T> T convertTo(Class<T> type) {
            return mType.convertTo(mValue, type);
        }
    }
}
