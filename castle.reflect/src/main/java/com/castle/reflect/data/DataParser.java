package com.castle.reflect.data;

import com.castle.reflect.exceptions.TypeException;
import com.castle.util.function.ThrowingFunction;

import java.util.HashMap;
import java.util.Map;

public class DataParser<KEY, TYPE extends DataType<KEY>, RAW, FULL> {

    public static class Builder<KEY, TYPE extends DataType<KEY>, RAW, FULL> {

        private final KnownDataTypes<KEY, TYPE> mTypes;
        private final Map<TYPE, ThrowingFunction<RAW, FULL, TypeException>> mParsers;

        public Builder(KnownDataTypes<KEY, TYPE> types) {
            mTypes = types;
            mParsers = new HashMap<>();
        }

        public Builder<KEY, TYPE, RAW, FULL> parser(TYPE type, ThrowingFunction<RAW, FULL, TypeException> parser) {
            mParsers.put(type, parser);
            return this;
        }

        public DataParser<KEY, TYPE, RAW, FULL> build() {
            return new DataParser<>(mTypes, mParsers);
        }
    }

    private final KnownDataTypes<KEY, TYPE> mTypes;
    private final Map<TYPE, ThrowingFunction<RAW, FULL, TypeException>> mParsers;

    public DataParser(KnownDataTypes<KEY, TYPE> types,
                      Map<TYPE, ThrowingFunction<RAW, FULL, TypeException>> parsers) {
        mTypes = types;
        mParsers = parsers;
    }

    public FULL parse(KEY typeKey, RAW raw) throws TypeException {
        TYPE type = mTypes.getFromKey(typeKey);
        return mParsers.get(type).apply(raw);
    }
}
