package com.castle.function;

import java.util.Collection;
import java.util.stream.Stream;

class ProcessorImpls {

    private ProcessorImpls() {}

    private static final Processor IDENTITY = new IdentityProcessor();
    private static final Processor FLATTEN = new FlattenProcessor();

    @SuppressWarnings("unchecked")
    public static <T, E extends Exception> Processor<T, T, E> identity() {
        return IDENTITY;
    }

    @SuppressWarnings("unchecked")
    public static <T, E extends Exception> Processor<Collection<T>, Stream<T>, E> flatten() {
        return FLATTEN;
    }

    private static class IdentityProcessor implements Processor {

        @Override
        public Object process(Object o) throws Exception {
            return o;
        }
    }

    private static class FlattenProcessor implements Processor<Collection, Stream, Exception> {
        @Override
        public Stream process(Collection collection) throws Exception {
            return collection.stream();
        }
    }
}
