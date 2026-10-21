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

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class GuavaCacheTest {

    @Test
    public void get_put_basic(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();

        cache.put("key1", "value1");
        assertThat(cache.get("key1")).isEqualTo("value1");
        assertThat(cache.get("missing")).isNull();
    }

    @Test
    public void evict_removesEntry(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();
        cache.put("key1", "value1");
        cache.evict("key1");
        assertThat(cache.get("key1")).isNull();
    }

    @Test
    public void clear_removesAll(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();
        cache.put("key1", "value1");
        cache.put("key2", "value2");
        cache.clear();
        assertThat(cache.get("key1")).isNull();
        assertThat(cache.get("key2")).isNull();
    }

    @Test
    public void exists_trueForPresentKey(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();
        cache.put("key1", "value1");
        assertThat(cache.exists("key1")).isTrue();
    }

    @Test
    public void exists_falseForMissingKey(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();
        assertThat(cache.exists("missing")).isFalse();
    }

    @Test
    public void computeIfAbsent_atomicLoad(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();
        AtomicInteger loadCount = new AtomicInteger(0);

        String result = cache.computeIfAbsent("key1", k -> {
            loadCount.incrementAndGet();
            return "computed";
        });

        assertThat(result).isEqualTo("computed");
        assertThat(loadCount.get()).isEqualTo(1);
    }

    @Test
    public void computeIfAbsent_existingValue_notReloaded(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();
        cache.put("key1", "existing");
        AtomicInteger loadCount = new AtomicInteger(0);

        String result = cache.computeIfAbsent("key1", k -> {
            loadCount.incrementAndGet();
            return "computed";
        });

        assertThat(result).isEqualTo("existing");
        assertThat(loadCount.get()).isEqualTo(0);
    }

    @Test
    public void computeIfAbsent_concurrentLoad_onlyOne(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();
        AtomicInteger loadCount = new AtomicInteger(0);

        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                cache.computeIfAbsent("key1", k -> {
                    loadCount.incrementAndGet();
                    return "computed";
                });
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        assertThat(loadCount.get()).isEqualTo(1);
        assertThat(cache.get("key1")).isEqualTo("computed");
    }

    @Test
    public void computeIfAbsent_nullMappingFunction_throwsInvalidCacheLoadException(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();

        // Guava's get(key, callable) throws InvalidCacheLoadException if the callable returns null.
        assertThatThrownBy(() -> cache.computeIfAbsent("key1", k -> null))
            .isInstanceOf(com.google.common.cache.CacheLoader.InvalidCacheLoadException.class);
    }

    @Test
    public void computeIfAbsent_throws_propagatesException(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder().build();

        // Guava wraps a RuntimeException thrown by the loader in UncheckedExecutionException.
        assertThatThrownBy(() -> cache.computeIfAbsent("key1", k -> {
            throw new RuntimeException("boom");
        })).isInstanceOf(com.google.common.util.concurrent.UncheckedExecutionException.class)
            .hasMessageContaining("boom")
            .hasCauseInstanceOf(RuntimeException.class);

        assertThat(cache.get("key1")).isNull();
    }

    @Test
    public void builder_customConfig(){
        GuavaCache<String, String> cache = GuavaCache.<String, String>builder()
            .setInitialCapacity(100)
            .setMaximumSize(500)
            .setExpireAfterWrite(java.time.Duration.ofMinutes(10))
            .build();

        cache.put("k", "v");
        assertThat(cache.get("k")).isEqualTo("v");
    }
}