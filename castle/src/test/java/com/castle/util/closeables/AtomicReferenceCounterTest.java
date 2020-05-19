package com.castle.util.closeables;

import org.junit.jupiter.api.Test;

import java.util.concurrent.atomic.AtomicInteger;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AtomicReferenceCounterTest {

    @Test
    public void decrement_lastReferenceCount_returnsTrue() throws Exception {
        final AtomicInteger count = new AtomicInteger(1);
        AtomicReferenceCounter counter = new AtomicReferenceCounter(count);

        boolean result = counter.decrement();
        assertTrue(result);
    }

    @Test
    public void decrement_noMoreReferences_throwsIllegalStateException() throws Exception {
        final AtomicInteger count = new AtomicInteger(0);
        AtomicReferenceCounter counter = new AtomicReferenceCounter(count);

        assertThrows(IllegalStateException.class, counter::decrement);
    }

    @Test
    public void decrement_severalReferencesLeft_returnsFalse() throws Exception {
        final AtomicInteger count = new AtomicInteger(3);
        AtomicReferenceCounter counter = new AtomicReferenceCounter(count);

        boolean result = counter.decrement();
        assertFalse(result);
    }

    @Test
    public void decrement_hasReferences_decreasesCount() throws Exception {
        final AtomicInteger count = new AtomicInteger(1);
        AtomicReferenceCounter counter = new AtomicReferenceCounter(count);

        counter.decrement();
        assertThat(count.get(), equalTo(0));
    }

    @Test
    public void increment_hasReferences_increasesCount() throws Exception {
        final AtomicInteger count = new AtomicInteger(1);
        AtomicReferenceCounter counter = new AtomicReferenceCounter(count);

        counter.increment();
        assertThat(count.get(), equalTo(2));
    }
}