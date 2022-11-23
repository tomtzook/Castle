package com.castle.util.closeables;

import com.castle.annotations.NotThreadSafe;
import com.castle.util.function.ThrowingSupplier;
import com.castle.util.throwables.ThrowableChain;
import com.castle.util.throwables.Throwables;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;

/**
 * A utility for managing multiple {@link AutoCloseable} (or {@link java.io.Closeable}
 * objects.
 *
 * Allows registration of <em>resources</em> which will all be closed upon invocations
 * of {@link #close()} or as a result of using {@link #run(ThrowingSupplier, Class, CloseOption)}.
 *
 * <p>
 *     Registering <em>resources</em> on the fly.
 *     <pre>
 *        try (Closer closer = Closer.empty()) {
 *              // do some stuff
 *              closer.add(resource1);
 *              // do some other stuff
 *              closer.add(resource2);
 *              // do more stuff
 *         }
 *     </pre>
 *     <em>resource1</em> and <em>resource2</em> will both be closed.
 * <p>
 *     Alternatively, using {@link #run(ThrowingSupplier, Class, CloseOption) run}
 *     <pre>
 *         Closer closer = Closer.with(resource);
 *         closer.run(()-&lt {
 *             // do some stuff with resource
 *         }, ExpectedException.class, Closer.CloserOption.ON_ERROR);
 *     </pre>
 */
@NotThreadSafe
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
        ON_SUCCESS {
            @Override
            boolean shouldClose(boolean wasExceptionThrown) {
                return !wasExceptionThrown;
            }
        };

        abstract boolean shouldClose(boolean wasExceptionThrown);
    }

    private final Deque<AutoCloseable> mCloseables;
    private boolean mIsClosed;

    public Closer(Deque<AutoCloseable> closeables) {
        mCloseables = closeables;
        mIsClosed = false;
    }

    public Closer() {
        this(new ArrayDeque<>());
    }

    public static Closer empty() {
        return new Closer();
    }

    public static Closer with(AutoCloseable... closeables) {
        return with(Arrays.asList(closeables));
    }

    public static Closer with(Collection<? extends AutoCloseable> closeables) {
        return new Closer(new ArrayDeque<>(closeables));
    }

    public Closer add(AutoCloseable closeable) {
        mCloseables.addFirst(closeable);
        return this;
    }

    public Closer addAll(AutoCloseable... closeables) {
        return addAll(Arrays.asList(closeables));
    }

    public Closer addAll(Collection<? extends AutoCloseable> closeables) {
        closeables.forEach(this::add);
        return this;
    }

    /**
     * Executes a given function utilizing registered resources.
     *
     * @param consumer function to execute
     * @param exceptionType error expected to be thrown by the function.
     * @param closeOption when to close the registered resources.
     *                    <ul>
     *                      <li>if {@link CloseOption#ALWAYS}, {@link #close()}
     *                      will always be invoked after calling the given function.</li>
     *                      <li>if {@link CloseOption#ON_ERROR}, {@link #close()}
     *                      will be invoked if the given function throws an exception.</li>
     *                      <li>{@link CloseOption#ON_SUCCESS}, {@link #close()}
     *                      will be invoked if the given function does not throw an exception.</li>
     *                    </ul>
     * @param <R> expected return type.
     * @param <E> expected exception type.
     * @return result from the given function.
     * @throws E from the function, if it throws one.
     */
    public <R, E extends Exception> R run(ThrowingSupplier<R, E> consumer, Class<E> exceptionType, CloseOption closeOption) throws E {
        checkIsClosed();

        boolean wasExceptionThrown = false;
        ThrowableChain throwableChain = Throwables.newChain();

        try {
            return consumer.get();
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

    /**
     * Closes all the registered resources, by invoking {@link AutoCloseable#close() close} on each.
     * If one of the resources produces an exception during <em>close</em>, the exception is stored
     * aside until all the other resources has being close, and only then is it thrown.
     *
     * Resources are closed in reverse order to their registration.
     *
     * @throws Exception if one of the resources throws an exception while {@link AutoCloseable#close() closing} it.
     */
    @Override
    public void close() throws Exception {
        checkIsClosed();

        ThrowableChain chain = Throwables.newChain();

        while (!mCloseables.isEmpty()) {
            AutoCloseable closeable = mCloseables.remove();
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
