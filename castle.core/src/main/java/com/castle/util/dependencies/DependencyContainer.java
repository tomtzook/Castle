package com.castle.util.dependencies;

import com.castle.annotations.Immutable;
import com.castle.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Function;

@Immutable
public class DependencyContainer {

    @NotThreadSafe
    public static class Builder {

        private final Collection<DependencySupplier> mSuppliers;
        private final Collection<Function<DependencyContainer, ? extends DependencySupplier>> mIntializers;

        public Builder() {
            mSuppliers = new HashSet<>();
            mIntializers = new ArrayList<>();
        }

        public Builder add(Function<DependencyContainer, ? extends DependencySupplier> initializer) {
            mIntializers.add(initializer);
            return this;
        }

        public Builder add(DependencySupplier supplier) {
            mSuppliers.add(supplier);
            return this;
        }

        public Builder add(DependencySupplier... suppliers) {
            return add(Arrays.asList(suppliers));
        }

        public Builder add(Collection<? extends DependencySupplier> suppliers) {
            mSuppliers.addAll(suppliers);
            return this;
        }

        public DependencyContainer build() {
            DependencyContainer container = new DependencyContainer(mSuppliers);
            for (Function<DependencyContainer, ? extends DependencySupplier> function : mIntializers) {
                DependencySupplier supplier = function.apply(container);
                mSuppliers.add(supplier);
            }

            return container;
        }
    }

    private final Collection<DependencySupplier> mSuppliers;

    public DependencyContainer(Collection<? extends DependencySupplier> suppliers) {
        mSuppliers = new ArrayList<>(suppliers);
    }

    public <T> T get(Class<T> type) {
        Optional<T> optional = tryGet(type);

        if (optional.isPresent()) {
            return optional.get();
        }

        throw new IllegalArgumentException("Dependency not found " + type);
    }

    public <T> Optional<T> tryGet(Class<T> type) {
        Object instance = null;
        for (DependencySupplier dependency : mSuppliers) {
            if (!dependency.supports(type)) {
                continue;
            }

            instance = dependency.get();
            break;
        }

        if (instance == null) {
            return Optional.empty();
        }

        return Optional.of(type.cast(instance));
    }

    public <T> Collection<T> getAllMatching(Class<T> type) {
        Collection<T> matching = new HashSet<>();
        for (DependencySupplier dependency : mSuppliers) {
            if (!dependency.supports(type)) {
                continue;
            }

            T object = type.cast(dependency.get());
            matching.add(object);
        }

        return matching;
    }
}
