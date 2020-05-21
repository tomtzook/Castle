package com.castle.util.dependencies;

import com.castle.annotations.Immutable;
import com.castle.annotations.NotThreadSafe;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;

@Immutable
public class DependencyContainer {

    @NotThreadSafe
    public static class Builder {

        private final Collection<DependencySupplier> mSuppliers;

        public Builder() {
            mSuppliers = new HashSet<>();
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
            return new DependencyContainer(mSuppliers);
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
}
