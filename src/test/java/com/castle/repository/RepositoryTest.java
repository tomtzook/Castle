package com.castle.repository;

import com.castle.testutil.collections.CollectionUtils;
import com.castle.testutil.collections.MapUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RepositoryTest {

    @RunWith(Parameterized.class)
    public static class RepositoryImplTest {

        @Parameterized.Parameter(0)
        public Function<Map<Object, Object>, Repository<Object, Object>> mRepositoryCreator;
        @Parameterized.Parameter(1)
        public Class<?> mImplType;
        @Parameterized.Parameter(2)
        public Map<Object, Object> mInnerMap;

        public Repository<Object, Object> mRepository;

        @Parameterized.Parameters(name = "repository({1})")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {(Function<Map<Object, Object>, Repository<Object, Object>>) InMemoryRepository::new, InMemoryRepository.class, new HashMap<>()}
            });
        }

        @Before
        public void setup() throws Exception {
            mInnerMap.clear();
            mRepository = mRepositoryCreator.apply(mInnerMap);
        }

        @Test
        public void exists_keyInRepository_returnsTrue() throws Exception {
            final Object KEY = new Object();
            mInnerMap.put(KEY, new Object());

            assertTrue(mRepository.exists(KEY));
        }

        @Test
        public void exists_keyNotInRepository_returnsFalse() throws Exception {
            final Object KEY = new Object();

            assertFalse(mRepository.exists(KEY));
        }

        @Test
        public void existsBatch_allKeysInRepository_returnsTrue() throws Exception {
            final Collection<Object> KEYS = Arrays.asList(
                    new Object(), new Object(), new Object()
            );
            mInnerMap.putAll(MapUtils.generateMapWithKeys(KEYS, Object::new));

            assertTrue(mRepository.exists(KEYS));
        }

        @Test
        public void existsBatch_someKeysInRepositorySomeAreNot_returnsFalse() throws Exception {
            final Collection<Object> KEYS_IN_MAP = Arrays.asList(
                    new Object(), new Object(), new Object()
            );
            final Collection<Object> KEYS_NOT_IN_MAP = Arrays.asList(
                    new Object(), new Object(), new Object()
            );

            mInnerMap.putAll(MapUtils.generateMapWithKeys(KEYS_IN_MAP, Object::new));

            assertFalse(mRepository.exists(CollectionUtils.merge(KEYS_IN_MAP, KEYS_NOT_IN_MAP)));
        }

        @Test
        public void existsBatch_allKeysNotInRepository_returnsFalse() throws Exception {
            final Collection<Object> KEYS = Arrays.asList(
                    new Object(), new Object(), new Object()
            );

            assertFalse(mRepository.exists(KEYS));
        }
    }
}
