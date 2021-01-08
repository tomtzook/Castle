package com.castle.util.closeables;

import org.junit.jupiter.api.Test;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.locks.Lock;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReferencedCloseableTest {

    @Test
    public void close_hasMoreReferences_doesNotCloseCloseable() throws Exception {
        Closeable closeable = mock(Closeable.class);
        ReferencedCloseable referencedCloseable = new FakeReferencedCloseable(
                referenceCounterWithReferences(3),
                mock(Lock.class),
                closeable);

        referencedCloseable.close();

        verify(closeable, never()).close();
    }

    @Test
    public void close_lastReference_closesCloseable() throws Exception {
        Closeable closeable = mock(Closeable.class);
        ReferencedCloseable referencedCloseable = new FakeReferencedCloseable(
                referenceCounterWithReferences(1),
                mock(Lock.class),
                closeable);

        referencedCloseable.close();

        verify(closeable, times(1)).close();
    }

    private static ReferenceCounter referenceCounterWithReferences(int references) {
        ReferenceCounter referenceCounter = mock(ReferenceCounter.class);
        when(referenceCounter.decrement()).thenReturn(references - 1 == 0);

        return referenceCounter;
    }

    private static class FakeReferencedCloseable extends ReferencedCloseable {

        private final Closeable mCloseable;

        public FakeReferencedCloseable(ReferenceCounter referenceCounter, Lock lock, Closeable closeable) {
            super(referenceCounter, lock);
            mCloseable = closeable;
        }

        @Override
        protected void doClose() throws IOException {
            mCloseable.close();
        }
    }
}