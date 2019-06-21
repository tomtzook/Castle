package com.castle.util.closeables;

import com.castle.util.function.ThrowingRunnable;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.util.ArrayDeque;
import java.util.Queue;

public class Closer implements AutoCloseable {

    public enum CloseOption {
        ALWAYS {
            @Override
            boolean shouldClose(boolean wasExceptionThrown) {
                return true;
            }
        },
        ON_ERROR {
            @Override
            boolean shouldClose(boolean wasExceptionThrown) {
                return wasExceptionThrown;
            }
        },
        ON_SUCESS {
            @Override
            boolean shouldClose(boolean wasExceptionThrown) {
                return !wasExceptionThrown;
            }
        };

        abstract boolean shouldClose(boolean wasExceptionThrown);
    }

    private final Queue<AutoCloseable> mCloseables;
    private boolean mIsClosed;

    public Closer(Queue<AutoCloseable> closeables) {
        mCloseables = closeables;
        mIsClosed = false;
    }

    public Closer() {
        this(new ArrayDeque<>());
    }

    public static Closer empty() {
        return new Closer();
    }

    public <T extends AutoCloseable> Closer add(T closeable) {
        mCloseables.add(closeable);
        return this;
    }

    public <R, E extends Exception> R run(ThrowingRunnable<R, E> consumer, Class<E> exceptionType, CloseOption closeOption) throws E {
        checkIsClosed();

        boolean wasExceptionThrown = false;
        ThrowableChain throwableChain = Throwables.newChain();

        try {
            return consumer.run();
        } catch (Throwable t) {
            wasExceptionThrown = true;
            throwableChain.chain(t);
        } finally {
            if (closeOption.shouldClose(wasExceptionThrown)) {
                try {
                    close();
                } catch (Throwable t) {
                    throwableChain.chain(t);
                }
            }

            throwableChain.throwIfType(exceptionType);
            throwableChain.throwAsRuntime();
        }

        // should not be reached. On exception, will be thrown from chain
        // on success, will be returned from try
        throw new IllegalStateException();
    }

    @Override
    public void close() throws Exception {
        checkIsClosed();

        ThrowableChain chain = Throwables.newChain();

        while (!mCloseables.isEmpty()) {
            AutoCloseable closeable  = mCloseables.remove();
            try {
                closeable.close();
            } catch (Throwable t) {
                chain.chain(t);
            }
        }

        mIsClosed = true;

        chain.throwIfType(Exception.class);
        chain.throwAsRuntime();
    }

    private void checkIsClosed() {
        if (mIsClosed) {
            throw new IllegalStateException("Closer is closed");
        }
    }
}
