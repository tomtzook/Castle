package com.castle.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Supplier;
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

    public static <T, R, E extends Exception> Processor<Collection<T>, Collection<R>, E>
            mapper(Function<? super T, ? extends R> mapper, Supplier<? extends Collection<R>> factory) {
        return new MappingProcessor<>(mapper, factory);
    }

    public static <T, R, E extends Exception> Processor<Collection<T>, Collection<R>, E>
            mapper(Function<? super T, ? extends R> mapper) {
        return mapper(mapper, ArrayList::new);
    }

    public static <T, R, E extends Exception> Pipeline<T, E> end(
            Processor<? super T, ? extends R, ? extends E> processor,
            Pipeline<? super R, ? extends E> pipeline) {
        return new ProcessorEnd<>(processor, pipeline);
    }

    @SuppressWarnings("unchecked")
    static <T, R, R2, E extends Exception> Processor<T, R2, E> chain(
            Processor<? super T, ? extends R, ? extends E> first,
            Processor<? super R, ? extends R2, ? extends E> second) {
        return new ProcessorChain(first, second);
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

    private static class MappingProcessor<T, R, E extends Exception> implements Processor<Collection<T>, Collection<R>, E> {

        private final Function<? super T, ? extends R> mMapper;
        private final Supplier<? extends Collection<R>> mFactory;

        private MappingProcessor(Function<? super T, ? extends R> mapper, Supplier<? extends Collection<R>> factory) {
            mMapper = mapper;
            mFactory = factory;
        }

        @Override
        public Collection<R> process(Collection<T> collection) throws E {
            Collection<R> result = mFactory.get();
            for (T t : collection) {
                result.add(mMapper.apply(t));
            }
            return result;
        }
    }

    private static class ProcessorEnd<T, R, E extends Exception> implements Pipeline<T, E> {

        private final Processor<? super T, ? extends R, ? extends E> mProcessor;
        private final Pipeline<? super R, ? extends E> mPipeline;

        public ProcessorEnd(Processor<? super T, ? extends R, ? extends E> processor,
                            Pipeline<? super R, ? extends E> pipeline) {
            mProcessor = processor;
            mPipeline = pipeline;
        }

        @Override
        public void process(T t) throws E {
            R r = mProcessor.process(t);
            mPipeline.process(r);
        }
    }
}
