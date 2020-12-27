package com.castle.function;

import com.castle.function.safe.SafeProcessor;
import com.castle.function.safe.SafeProcessorChain;
import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;

import java.util.Collection;
import java.util.stream.Stream;

public interface Processor<T, R, E extends Exception> {

    R process(T t) throws E;

    default <R2> Processor<T, R2, E> andThen(Processor<? super R, ? extends R2, ? extends E> processor) {
        return ProcessorImpls.chain(this, processor);
    }

    default Pipeline<T, E> pipeTo(Pipeline<? super R, ? extends E> pipeline) {
        return ProcessorImpls.end(this, pipeline);
    }

    default SafeProcessor<T, R> safe() {
        return safe(Throwables.silentHandler());
    }

    default SafeProcessor<T, R> safe(ThrowableHandler handler) {
        return SafeProcessorChain.create(this, handler);
    }

    static <T, E extends Exception> Processor<T, T, E> identity() {
        return ProcessorImpls.identity();
    }

    static <T, E extends Exception> Processor<T, T, E> identity(Class<T> input, Class<E> ex) {
        return ProcessorImpls.identity();
    }

    static <T, E extends Exception> Processor<Collection<T>, Stream<T>, E> flatten() {
        return ProcessorImpls.flatten();
    }
}
