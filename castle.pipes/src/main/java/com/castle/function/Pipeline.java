package com.castle.function;

import com.castle.function.safe.SafePipeJunction;
import com.castle.function.safe.SafePipeline;
import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;

import java.util.function.Function;

public interface Pipeline<T, E extends Exception> {

    void process(T t) throws E;

    default Pipeline<T, E> divergeTo(Pipeline<? super T, ? extends E> pipeline) {
        return PipeJunction.create(this, pipeline);
    }

    default <T2> Pipeline<T2, E> mapTo(Function<? super T2, ? extends T> mapper) {
        return new MappingPipe<>(this, mapper);
    }

    default SafePipeline<T> safe() {
        return safe(Throwables.silentHandler());
    }

    default SafePipeline<T> safe(ThrowableHandler handler) {
        return SafePipeJunction.create(this, handler);
    }
}
