package com.castle.function;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

class ProcessorChain implements Processor {

    private final Collection<Processor> mProcessors;

    ProcessorChain(Processor... processors) {
        mProcessors = new ArrayList<>(processors.length);
        Collections.addAll(mProcessors, processors);
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
