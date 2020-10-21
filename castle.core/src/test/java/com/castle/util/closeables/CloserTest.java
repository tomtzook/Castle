package com.castle.util.closeables;

import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.collection.IsArrayWithSize.arrayWithSize;
import static org.hamcrest.collection.IsIterableContainingInRelativeOrder.containsInRelativeOrder;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

public class CloserTest {

    @Test
    public void add_forCloseable_addsToFront() throws Exception {
        final AutoCloseable CLOSEABLE = mock(AutoCloseable.class);

        Deque<AutoCloseable> closeablesDeque = new ArrayDeque<>();
        closeablesDeque.addAll(Arrays.asList(
                mock(AutoCloseable.class),
                mock(AutoCloseable.class),
                mock(AutoCloseable.class)
        ));
        Closer closer = new Closer(closeablesDeque);

        closer.add(CLOSEABLE);

        assertThat(closeablesDeque.remove(), equalTo(CLOSEABLE));
    }

    @Test
    public void add_forMultipleCloseables_addsByOrderToFront() throws Exception {
        final AutoCloseable CLOSEABLE1 = mock(AutoCloseable.class);
        final AutoCloseable CLOSEABLE2 = mock(AutoCloseable.class);

        Deque<AutoCloseable> closeablesDeque = new ArrayDeque<>();
        closeablesDeque.addAll(Arrays.asList(
                mock(AutoCloseable.class),
                mock(AutoCloseable.class),
                mock(AutoCloseable.class)
        ));
        Closer closer = new Closer(closeablesDeque);

        closer.add(CLOSEABLE1);
        closer.add(CLOSEABLE2);

        assertThat(closeablesDeque.remove(), equalTo(CLOSEABLE2));
        assertThat(closeablesDeque.remove(), equalTo(CLOSEABLE1));
    }

    @Test
    public void addAll_forCloseablesCollection_addsByOrderToFront() throws Exception {
        final List<AutoCloseable> CLOSEABLES = Arrays.asList(
                mock(AutoCloseable.class),
                mock(AutoCloseable.class),
                mock(AutoCloseable.class)
        );

        Deque<AutoCloseable> closeablesDeque = new ArrayDeque<>();
        closeablesDeque.addAll(Arrays.asList(
                mock(AutoCloseable.class),
                mock(AutoCloseable.class),
                mock(AutoCloseable.class)
        ));
        Closer closer = new Closer(closeablesDeque);

        closer.addAll(CLOSEABLES);

        Collections.reverse(CLOSEABLES);
        assertThat(closeablesDeque, containsInRelativeOrder(CLOSEABLES.toArray()));
        assertThat(closeablesDeque.peekFirst(), equalTo(CLOSEABLES.get(0)));
    }

    @Test
    public void close_forMultipleCloseables_closesInOrder() throws Exception {
        final List<AutoCloseable> CLOSEABLES = Arrays.asList(
                mock(AutoCloseable.class),
                mock(AutoCloseable.class),
                mock(AutoCloseable.class)
        );

        Deque<AutoCloseable> closeablesDeque = new ArrayDeque<>();
        closeablesDeque.addAll(CLOSEABLES);
        Closer closer = new Closer(closeablesDeque);

        closer.close();

        InOrder inOrder = Mockito.inOrder(CLOSEABLES.toArray());
        for (AutoCloseable closeable : CLOSEABLES) {
            inOrder.verify(closeable).close();
        }
    }

    @Test
    public void close_forMultipleCloseablesWithExceptionThrown_closesInOrder() throws Exception {
        final List<AutoCloseable> CLOSEABLES = Arrays.asList(
                mock(AutoCloseable.class),
                mockThrowingCloseable(),
                mock(AutoCloseable.class)
        );

        Deque<AutoCloseable> closeablesDeque = new ArrayDeque<>();
        closeablesDeque.addAll(CLOSEABLES);
        Closer closer = new Closer(closeablesDeque);

        try {
            closer.close();
            fail("exception expected");
        } catch (Exception e) {
            // expected
        }

        InOrder inOrder = Mockito.inOrder(CLOSEABLES.toArray());
        for (AutoCloseable closeable : CLOSEABLES) {
            inOrder.verify(closeable).close();
        }
    }

    @Test
    public void close_forMultipleCloseablesWithExceptionThrown_propagatesException() throws Exception {
        assertThrows(Exception.class, () -> {
            final List<AutoCloseable> CLOSEABLES = Arrays.asList(
                    mock(AutoCloseable.class),
                    mockThrowingCloseable(),
                    mock(AutoCloseable.class)
            );

            Deque<AutoCloseable> closeablesDeque = new ArrayDeque<>();
            closeablesDeque.addAll(CLOSEABLES);
            Closer closer = new Closer(closeablesDeque);

            closer.close();
        });
    }

    @Test
    public void close_forMultipleCloseablesWithExceptionThrown_propagatesExceptionAndSuppressesOther() throws Exception {
        Throwable thrownException = assertThrows(Exception.class, () -> {
            final List<AutoCloseable> CLOSEABLES = Arrays.asList(
                    mock(AutoCloseable.class),
                    mockThrowingCloseable(),
                    mockThrowingCloseable()
            );

            Deque<AutoCloseable> closeablesDeque = new ArrayDeque<>();
            closeablesDeque.addAll(CLOSEABLES);
            Closer closer = new Closer(closeablesDeque);

            closer.close();
        });

        assertThat(thrownException.getSuppressed(), arrayWithSize(1));
    }

    private AutoCloseable mockThrowingCloseable() throws Exception {
        AutoCloseable closeable = mock(AutoCloseable.class);
        doThrow(new Exception()).when(closeable).close();

        return closeable;
    }
}