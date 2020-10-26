package com.castle.store;

import com.castle.store.exceptions.KeyNotFoundException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.hamcrest.collection.IsMapWithSize.aMapWithSize;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KeyValueStoreTest {

    @ParameterizedTest(name = "{index} => impl {0}: store({1}, {2})")
    @MethodSource("argumentsKeyValue")
    public void store_newValue_valueInMap(ImplFactory impl, Object key, Object value) throws Exception {
        Implementation implementation = impl.create();
        KeyValueStore keyValueStore = implementation.getImplementation();

        keyValueStore.store(key, value);

        Map<Object, Object> values = implementation.getCurrentStoredValues();
        assertThat(values, hasEntry(key, value));
        assertThat(values, aMapWithSize(1));
    }

    @ParameterizedTest(name = "{index} => impl {0}: store({1}, {2}) == Optional.empty()")
    @MethodSource("argumentsKeyValue")
    public void store_newValue_returnsEmptyOptional(ImplFactory impl, Object key, Object value) throws Exception {
        Implementation implementation = impl.create();
        KeyValueStore keyValueStore = implementation.getImplementation();

        Optional<Object> optional = keyValueStore.store(key, value);
        assertThat(optional.isPresent(), equalTo(false));
        assertThrows(NoSuchElementException.class, optional::get);
    }

    @ParameterizedTest(name = "{index} => impl {0}: store({1}, {2}) == Optional.of({3})")
    @MethodSource("argumentsKeyNewValueOldValue")
    public void store_keyHasValue_returnsOptionalWithValue(ImplFactory impl, Object key,
                                                           Object newValue, Object oldValue) throws Exception {
        Map<Object, Object> map = new HashMap<>();
        map.put(key, oldValue);

        Implementation implementation = impl.create(map);
        KeyValueStore keyValueStore = implementation.getImplementation();

        Optional<Object> optional = keyValueStore.store(key, newValue);
        assertThat(optional.isPresent(), equalTo(true));
        assertThat(optional.get(), equalTo(oldValue));
    }

    @ParameterizedTest(name = "{index} => impl {0}: store({1}, {2}) == Optional.of({3})")
    @MethodSource("argumentsKeyValueType")
    public void retrieve_keyHasValue_returnsValue(ImplFactory impl, Object key, Object value, Class<?> type) throws Exception {
        Map<Object, Object> map = new HashMap<>();
        map.put(key, value);

        Implementation implementation = impl.create(map);
        KeyValueStore keyValueStore = implementation.getImplementation();

        Object returnValue = keyValueStore.retrieve(key, type);
        assertThat(returnValue, equalTo(value));
        assertTrue(type.isInstance(returnValue));
    }

    @ParameterizedTest(name = "{index} => impl {0}: store({1}, {2}) == Optional.of({3})")
    @MethodSource("argumentsKeyValueType")
    public void retrieve_keyNotExists_throwsKeyNotFoundException(ImplFactory impl, Object key, Object value, Class<?> type) throws Exception {
        Map<Object, Object> map = new HashMap<>();

        Implementation implementation = impl.create(map);
        KeyValueStore keyValueStore = implementation.getImplementation();

        assertThrows(KeyNotFoundException.class, () -> {
            keyValueStore.retrieve(key, type);
        });
    }

    @ParameterizedTest(name = "{index} => impl {0}: store({1}, {2}) == Optional.of({3})")
    @MethodSource("argumentsKeyOldNewValueType")
    public void putAndRetrieve_keyChanged_returnsUpdatedValue(ImplFactory impl, Object key, Object oldValue, Object newValue, Class<?> type) throws Exception {
        Map<Object, Object> map = new HashMap<>();
        map.put(key, oldValue);

        Implementation implementation = impl.create(map);
        KeyValueStore keyValueStore = implementation.getImplementation();
        keyValueStore.store(key, newValue);

        Object returnValue = keyValueStore.retrieve(key, type);
        assertThat(returnValue, equalTo(newValue));
        assertTrue(type.isInstance(returnValue));
        assertThat(returnValue, not(equalTo(oldValue)));
    }

    private static Stream<Arguments> argumentsKeyValue() {
        Collection<Arguments> arguments = new ArrayList<>();
        for (ImplFactory implFactory : implementations()) {
            arguments.add(Arguments.of(implFactory, "key", "value"));
        }

        return arguments.stream();
    }

    private static Stream<Arguments> argumentsKeyNewValueOldValue() {
        Collection<Arguments> arguments = new ArrayList<>();
        for (ImplFactory implFactory : implementations()) {
            arguments.add(Arguments.of(implFactory, "key", "value", "oldValue"));
        }

        return arguments.stream();
    }

    private static Stream<Arguments> argumentsKeyValueType() {
        Collection<Arguments> arguments = new ArrayList<>();
        for (ImplFactory implFactory : implementations()) {
            arguments.add(Arguments.of(implFactory, "key", "value", String.class));
        }

        return arguments.stream();
    }

    private static Stream<Arguments> argumentsKeyOldNewValueType() {
        Collection<Arguments> arguments = new ArrayList<>();
        for (ImplFactory implFactory : implementations()) {
            arguments.add(Arguments.of(implFactory, "key", "old", "new", String.class));
        }

        return arguments.stream();
    }

    private static Collection<ImplFactory> implementations() {
        return Arrays.asList(
                new ConcurrentInMemoryImplementation.Factory()
        );
    }

    interface Implementation {

        KeyValueStore getImplementation();

        Map<Object, Object> getCurrentStoredValues();
    }

    interface ImplFactory {

        Implementation create();

        Implementation create(Map<?, ?> values);
    }
}