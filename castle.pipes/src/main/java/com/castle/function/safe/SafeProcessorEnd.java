package com.castle.function.safe;

public class SafeProcessorEnd<T, R> implements SafePipeline<T> {

    private final SafeProcessor<? super T, ? extends R> mProcessor;
    private final SafePipeline<? super R> mPipeline;

    public SafeProcessorEnd(SafeProcessor<? super T, ? extends R> processor,
                            SafePipeline<? super R> pipeline) {
        mProcessor = processor;
        mPipeline = pipeline;
    }

    @Override
    public void process(T t) {
        R r = mProcessor.process(t);
        mPipeline.process(r);
    }
}
