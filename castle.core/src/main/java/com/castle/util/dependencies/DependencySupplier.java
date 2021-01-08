package com.castle.util.dependencies;

import com.castle.annotations.Immutable;
import com.castle.annotations.ThreadSafe;

import java.util.function.Predicate;
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
            mReference = null;
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

    @ThreadSafe
    class FunctionalLazy extends Lazy {

        private final Predicate<Class<?>> mSupportFunction;
        private final Supplier<Object> mCreateFunction;

        public FunctionalLazy(Predicate<Class<?>> supportFunction, Supplier<Object> createFunction) {
            mSupportFunction = supportFunction;
            mCreateFunction = createFunction;
        }

        @Override
        public boolean supports(Class<?> type) {
            return mSupportFunction.test(type);
        }

        @Override
        Object createInstance() {
            return mCreateFunction.get();
        }
    }
}
