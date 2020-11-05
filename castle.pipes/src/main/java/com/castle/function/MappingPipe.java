package com.castle.function;

import java.util.function.Function;

class MappingPipe<T, T2, E extends Exception> implements Pipeline<T, E> {

    private final Pipeline<? super T2, ? extends E> mPipeline;
    private final Function<? super T, ? extends T2> mMapper;

    public MappingPipe(Pipeline<? super T2, ? extends E> pipeline,
                       Function<? super T, ? extends T2> mapper) {
        mPipeline = pipeline;
        mMapper = mapper;
    }

    @Override
    public void process(T t) throws E {
        T2 t2 = mMapper.apply(t);
        mPipeline.process(t2);
    }
}
