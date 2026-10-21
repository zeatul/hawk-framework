/*
 * Copyright 2025-2026 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package glz.hawkframework.core.cache;

import org.junit.jupiter.api.Test;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class DefaultCacheExecutorTest {

    @Test
    public void execute_cacheMiss_loadsValue(){
        MapCache<String, String> cache = new MapCache<>();
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        String result = executor.execute("key1", () -> "value1");

        assertThat(result).isEqualTo("value1");
        assertThat(cache.get("key1")).isEqualTo("value1");
    }

    @Test
    public void execute_cacheHit_returnsCachedValue(){
        MapCache<String, String> cache = new MapCache<>();
        cache.put("key1", "cached");
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        String result = executor.execute("key1", () -> "should-not-be-called");

        assertThat(result).isEqualTo("cached");
    }

    @Test
    public void execute_nullLoaderResult_returnsNull(){
        MapCache<String, String> cache = new MapCache<>();
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        String result = executor.execute("key1", () -> null);

        assertThat(result).isNull();
        // A null result should not be cached; calling again will re-execute the loader
        String result2 = executor.execute("key1", () -> "value2");
        assertThat(result2).isEqualTo("value2");
    }

    @Test
    public void execute_concurrentMiss_onlyOneLoaderCalled(){
        MapCache<String, String> cache = new MapCache<>();
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);
        AtomicInteger loadCount = new AtomicInteger(0);

        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                executor.execute("key1", () -> {
                    loadCount.incrementAndGet();
                    return "value";
                });
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        // Only one load should happen due to atomic computeIfAbsent
        assertThat(loadCount.get()).isEqualTo(1);
        assertThat(cache.get("key1")).isEqualTo("value");
    }

    @Test
    public void evict_removesEntry(){
        MapCache<String, String> cache = new MapCache<>();
        cache.put("key1", "value1");
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        executor.evict("key1");

        assertThat(cache.get("key1")).isNull();
        assertThat(executor.exists("key1")).isFalse();
    }

    @Test
    public void clear_removesAllEntries(){
        MapCache<String, String> cache = new MapCache<>();
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        executor.clear();

        assertThat(executor.exists("key1")).isFalse();
        assertThat(executor.exists("key2")).isFalse();
    }

    @Test
    public void exists_trueForCachedValue(){
        MapCache<String, String> cache = new MapCache<>();
        cache.put("key1", "value1");
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        assertThat(executor.exists("key1")).isTrue();
    }

    @Test
    public void exists_falseForAbsentKey(){
        MapCache<String, String> cache = new MapCache<>();
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        assertThat(executor.exists("key1")).isFalse();
    }

    @Test
    public void constructor_nullCache_throws(){
        assertThatThrownBy(() -> new DefaultCacheExecutor<>(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void execute_nullKey_throws(){
        MapCache<String, String> cache = new MapCache<>();
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        assertThatThrownBy(() -> executor.execute(null, () -> "value"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void execute_nullLoader_throws(){
        MapCache<String, String> cache = new MapCache<>();
        DefaultCacheExecutor<String, String> executor = new DefaultCacheExecutor<>(cache);

        assertThatThrownBy(() -> executor.execute("key1", null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    // Simple map-based cache for testing
    private static class MapCache<K, V> implements Cache<K, V> {
        private final ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();

        @Override
        public V get(@Nonnull K key) {
            return map.get(key);
        }

        @Override
        public void put(K key, V value) {
            map.put(key, value);
        }

        @Override
        public void evict(K key) {
            map.remove(key);
        }

        @Override
        public void clear() {
            map.clear();
        }

        @Override
        public boolean exists(K key) {
            return map.containsKey(key);
        }
    }
}
