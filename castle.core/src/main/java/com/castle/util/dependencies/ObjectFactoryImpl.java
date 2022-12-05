package com.castle.util.dependencies;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

public class ObjectFactoryImpl implements ObjectFactory {

    private final DependencyContainer mContainer;

    public ObjectFactoryImpl(DependencyContainer container) {
        mContainer = container;
    }

    @Override
    public <T> T create(Class<T> type) {
        Constructor<?>[] ctors = type.getConstructors();
        for (Constructor<?> ctor : ctors) {
            if (ctor.getAnnotation(Inject.class) == null) {
                continue;
            }

            Optional<Object[]> optionalDep = getDependenciesForCtor(ctor);
            if (!optionalDep.isPresent()) {
                continue;
            }

            try {
                Object[] dependencies = optionalDep.get();
                Object result = ctor.newInstance(dependencies);
                T resultT = type.cast(result);

                // put result in container for future use
                mContainer.add(new DependencySupplier.Static(resultT));

                return resultT;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new ObjectCreationException("failed creating instance with ctor: " + ctor, e);
            }
        }

        throw new ObjectCreationException("Usable constructor not found for type: " + type.getName());
    }

    private Optional<Object[]> getDependenciesForCtor(Constructor<?> ctor) {
        Class<?>[] params = ctor.getParameterTypes();
        Object[] dependencies = new Object[params.length];
        int dependenciesIndex = 0;
        for (Class<?> param : params) {
            Optional<?> optionalValue = mContainer.tryGet(param);
            if (!optionalValue.isPresent()) {
                return Optional.empty();
            }

            dependencies[dependenciesIndex++] = optionalValue.get();
        }

        return Optional.of(dependencies);
    }
}
