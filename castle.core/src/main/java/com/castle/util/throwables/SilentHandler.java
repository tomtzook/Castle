package com.castle.util.throwables;

import com.castle.annotations.Immutable;

@Immutable
public class SilentHandler implements ThrowableHandler {

    @Override
    public void handle(Throwable throwable) {
    }
}
