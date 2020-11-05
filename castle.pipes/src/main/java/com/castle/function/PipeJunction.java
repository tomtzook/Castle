package com.castle.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class PipeJunction implements Pipeline {

    private final Collection<Pipeline> mPipelines;

    private PipeJunction(Pipeline... pipelines) {
        mPipelines = new ArrayList<>(pipelines.length);
        Collections.addAll(mPipelines, pipelines);
    }

    @SuppressWarnings("unchecked")
    static <T, E extends Exception> Pipeline<T, E> create(Pipeline<? super T, ? extends E> first,
                                                          Pipeline<? super T, ? extends E> second) {
        return new PipeJunction(first, second);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void process(Object value) throws Exception {
        for (Pipeline pipeline : mPipelines) {
            pipeline.process(value);
        }
    }

    @Override
    public Pipeline divergeTo(Pipeline pipeline) {
        mPipelines.add(pipeline);
        return this;
    }
}
