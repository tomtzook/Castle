package com.castle.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class ProcessorChain implements Processor {

    private final Collection<Processor> mProcessors;

    private ProcessorChain(Processor... processors) {
        mProcessors = new ArrayList<>(processors.length);
        Collections.addAll(mProcessors, processors);
    }

    @SuppressWarnings("unchecked")
    static <T, R, R2, E extends Exception> Processor<T, R2, E> create(
            Processor<? super T, ? extends R, ? extends E> first,
            Processor<? super R, ? extends R2, ? extends E> second) {
        return new ProcessorChain(first, second);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object process(Object input) throws Exception {
        Object value = input;
        for (Processor processor : mProcessors) {
            value = processor.process(value);
        }
        return value;
    }

    @Override
    public Processor andThen(Processor processor) {
        mProcessors.add(processor);
        return this;
    }
}
