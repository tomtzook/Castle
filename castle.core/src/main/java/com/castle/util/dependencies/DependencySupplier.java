package com.castle.util.dependencies;

import com.castle.annotations.Immutable;
import com.castle.annotations.ThreadSafe;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

public interface DependencySupplier extends Supplier<Object> {

    boolean supports(Class<?> type);

    @Override
    Object get();

    @Immutable
    class Static implements DependencySupplier {

        private final Object mReference;

        public Static(Object reference) {
            mReference = reference;
        }

        @Override
        public boolean supports(Class<?> type) {
            return type.isInstance(mReference);
        }

        @Override
        public Object get() {
            return mReference;
        }
    }

    @ThreadSafe
    abstract class Lazy implements DependencySupplier {

        private Object mReference;

        protected Lazy() {
            mReference = new AtomicReference<>();
        }

        @Override
        public Object get() {
            if (mReference == null) {
                synchronized (this) {
                    if (mReference == null) {
                        mReference = createInstance();
                    }
                }
            }

            return mReference;
        }

        abstract Object createInstance();
    }
}
