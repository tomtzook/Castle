package com.castle.function.safe;

import com.castle.function.Pipeline;
import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SafePipeJunction implements SafePipeline {

    private final ThrowableHandler mThrowableHandler;
    private final Collection<Pipeline> mPipelines;

    public SafePipeJunction(ThrowableHandler throwableHandler, Pipeline... pipelines) {
        mThrowableHandler = throwableHandler;

        mPipelines = new ArrayList<>(pipelines.length);
        Collections.addAll(mPipelines, pipelines);
    }

    @SuppressWarnings("unchecked")
    public static <T, E extends Exception> SafePipeline<T> create(Pipeline<? super T, ? extends E> pipeline,
                                                                  ThrowableHandler throwableHandler) {
        return new SafePipeJunction(throwableHandler, pipeline);
    }

    @SuppressWarnings("unchecked")
    static <T, E extends Exception> SafePipeline<T> create(SafePipeline<? super T> first,
                                                           SafePipeline<? super T> second) {
        return new SafePipeJunction(Throwables.silentHandler(), first, second);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Object o) {
        for (Pipeline pipeline : mPipelines) {
            try {
                pipeline.process(o);
            } catch (Throwable throwable) {
                mThrowableHandler.handle(throwable);
            }
        }
    }

    @Override
    public SafePipeline divergeTo(SafePipeline pipeline) {
        mPipelines.add(pipeline);
        return this;
    }
}
