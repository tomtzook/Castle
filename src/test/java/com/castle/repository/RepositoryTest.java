package com.castle.repository;

import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class RepositoryTest {

    @RunWith(Parameterized.class)
    public static class RepositoryImplTest {

        @Parameterized.Parameter(0)
        public Repository<Object, Object> mRepository;
        @Parameterized.Parameter(1)
        public Class<?> mImplType;
        @Parameterized.Parameter(2)
        public Map<Object, Object> mInnerMap;

        @Parameterized.Parameters(name = "repository({1})")
        public static Collection<Object[]> data() {
            return Arrays.asList(new Object[][]{
                    {}
            });
        }
    }
}
