package com.castle.function.safe;

import com.castle.function.Pipeline;
import com.castle.util.throwables.ThrowableHandler;

import java.util.function.Function;

public interface SafePipeline<T> extends Pipeline<T, Exception> {

    @Override
    void process(T t);

    default SafePipeline<T> divergeTo(SafePipeline<? super T> pipeline) {
        return SafePipeJunction.create(this, pipeline);
    }

    default <T2> SafePipeline<T2> mapTo(Function<? super T2, ? extends T> mapper) {
        return new SafeMappingPipe<>(this, mapper);
    }

    @Override
    default SafePipeline<T> safe() {
        return this;
    }

    @Override
    default SafePipeline<T> safe(ThrowableHandler handler) {
        return this;
    }
}
