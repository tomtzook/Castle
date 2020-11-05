package com.castle.function.safe;

import com.castle.function.Processor;
import com.castle.util.throwables.ThrowableHandler;

public interface SafeProcessor<T, R> extends Processor<T, R, Exception> {

    @Override
    R process(T t);

    default <R2> SafeProcessor<T, R2> andThen(SafeProcessor<? super R, ? extends R2> processor) {
        return SafeProcessorChain.create(this, processor);
    }

    default SafePipeline<T> pipeTo(SafePipeline<? super R> pipeline) {
        return new SafeProcessorEnd<>(this, pipeline);
    }

    @Override
    default SafeProcessor<T, R> safe() {
        return this;
    }

    @Override
    default SafeProcessor<T, R> safe(ThrowableHandler handler) {
        return this;
    }
}
