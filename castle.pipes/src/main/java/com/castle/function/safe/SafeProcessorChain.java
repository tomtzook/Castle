package com.castle.function.safe;

import com.castle.function.Processor;
import com.castle.util.throwables.ThrowableHandler;
import com.castle.util.throwables.Throwables;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class SafeProcessorChain implements SafeProcessor {

    private final ThrowableHandler mThrowableHandler;
    private final Collection<Processor> mProcessors;

    private SafeProcessorChain(ThrowableHandler throwableHandler, Processor... processors) {
        mThrowableHandler = throwableHandler;
        mProcessors = new ArrayList<>(processors.length);
        Collections.addAll(mProcessors, processors);
    }

    @SuppressWarnings("unchecked")
    static <T, R, R2> SafeProcessor<T, R2> create(
            SafeProcessor<? super T, ? extends R> first,
            SafeProcessor<? super R, ? extends R2> second) {
        return new SafeProcessorChain(Throwables.silentHandler(), first, second);
    }

    @SuppressWarnings("unchecked")
    public static <T, R, E extends Exception> SafeProcessor<T, R> create(
            Processor<? super T, ? extends R, E> processor,
            ThrowableHandler throwableHandler) {
        return new SafeProcessorChain(throwableHandler, processor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object process(Object input) {
        try {
            Object value = input;
            for (Processor processor : mProcessors) {
                value = processor.process(value);
            }
            return value;
        } catch (Throwable t) {
            mThrowableHandler.handle(t);
            return null;
        }
    }
}
