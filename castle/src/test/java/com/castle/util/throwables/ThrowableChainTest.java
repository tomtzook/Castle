package com.castle.util.throwables;

import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.in;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class ThrowableChainTest {

    @Test
    public void chain_hasNone_storesAsTopThrowable() throws Exception {
        Throwable throwable = mock(Throwable.class);
        ThrowableChain chain = new ThrowableChain();

        chain.chain(throwable);

        Optional<Throwable> optional = chain.getTopThrowable();
        assertTrue(optional.isPresent());
        assertThat(optional.get(), equalTo(throwable));
    }

    @Test
    public void chain_hasTop_suppresses() throws Exception {
        Throwable throwable = mock(Throwable.class);
        ThrowableChain chain = new ThrowableChain(new Exception());

        chain.chain(throwable);

        Optional<Throwable> optional = chain.getTopThrowable();
        assertTrue(optional.isPresent());
        assertThat(optional.get(), not(equalTo(throwable)));

        assertThat(throwable, in(optional.get().getSuppressed()));
    }
}