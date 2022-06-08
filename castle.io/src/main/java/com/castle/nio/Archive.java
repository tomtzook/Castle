package com.castle.nio;

import com.castle.util.function.ThrowingConsumer;
import com.castle.util.function.ThrowingFunction;

import java.io.Closeable;
import java.io.IOException;

public interface Archive<T extends Closeable> {

    T open() throws IOException;

    default void openAndDo(ThrowingConsumer<T, IOException> operation) throws IOException {
        try (T open = open()) {
            operation.accept(open);
        }
    }

    default  <R> R openAndDo(ThrowingFunction<T, R, IOException> operation) throws IOException {
        try (T open = open()) {
            return operation.apply(open);
        }
    }
}
