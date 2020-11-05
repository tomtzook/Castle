package com.castle.function.safe;

import java.util.function.Function;

public class SafeMappingPipe<T, T2> implements SafePipeline<T> {

    private final SafePipeline<? super T2> mPipeline;
    private final Function<? super T, ? extends T2> mMapper;

    public SafeMappingPipe(SafePipeline<? super T2> pipeline,
                           Function<? super T, ? extends T2> mapper) {
        mPipeline = pipeline;
        mMapper = mapper;
    }

    @Override
    public void process(T t) {
        T2 t2 = mMapper.apply(t);
        mPipeline.process(t2);
    }
}
