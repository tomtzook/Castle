package com.castle.function;

class ProcessorEnd<T, R, E extends Exception> implements Pipeline<T, E> {

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
