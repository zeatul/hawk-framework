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

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.checkerframework.checker.index.qual.NonNegative;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * This class is responsible for
 * <p>Wraps Caffeine</p>
 * <p>Better than Guava</p>
 *
 * @author Zhang Peng
 */
public class CaffeineCache<K, V> implements Cache<K, V> {

    private com.github.benmanes.caffeine.cache.Cache<K, V> cache;

    private CaffeineCache(com.github.benmanes.caffeine.cache.Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    public @Nullable V get(@NonNull K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(@NonNull K key, @NonNull V value) {
        cache.put(key, value);
    }

    @Override
    public @Nullable V computeIfAbsent(@NonNull K key, @NonNull Function<? super K, ? extends V> mappingFunction) {
        return cache.get(key, mappingFunction::apply);
    }

    @Override
    public void evict(K key) {
        cache.invalidate(key);
    }

    @Override
    public void clear() {
        cache.invalidateAll();
    }

    @Override
    public boolean exists(K key) {
        return cache.asMap().containsKey(key);
    }

    public static class CaffeineCacheBuilder<K, V> {
        private int initialCapacity = 1000;
        private long maximumSize = 10000;
        private Duration expireAfterWrite = Duration.ofHours(2);

        public CaffeineCacheBuilder<K, V> setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        public CaffeineCacheBuilder<K, V> setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public CaffeineCacheBuilder<K, V> setExpireAfterWrite(Duration expireAfterWrite) {
            this.expireAfterWrite = expireAfterWrite;
            return this;
        }

        public CaffeineCacheBuilder<K, V> setExpireAfterWrite(long duration, TimeUnit unit) {
            this.expireAfterWrite = Duration.ofMillis(unit.toMillis(duration));
            return this;
        }

        public CaffeineCache<K, V> build() {
            com.github.benmanes.caffeine.cache.Cache<K, V> cache = Caffeine.newBuilder()
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfter(new CreateExpiry<>(expireAfterWrite))
                .build();
            return new CaffeineCache<>(cache);
        }
    }

    public static <K, V> CaffeineCacheBuilder<K, V> builder() {
        return new CaffeineCacheBuilder<>();
    }

    public static class CreateExpiry<K, V> implements Expiry<K, V> {
        /**
         * Nanoseconds
         */
        private final long base;

        public CreateExpiry(Duration expireAfterWrite) {
            this.base = expireAfterWrite.toNanos();
        }

        @Override
        public long expireAfterCreate(@NonNull K k, @NonNull V v, long l) {
            return base + ThreadLocalRandom.current().nextLong(base);
        }

        @Override
        public long expireAfterUpdate(@NonNull K k, @NonNull V v, long l, @NonNegative long l1) {
            return base + ThreadLocalRandom.current().nextLong(base);
        }

        @Override
        public long expireAfterRead(@NonNull K k, @NonNull V v, long l, @NonNegative long l1) {
            return Long.MAX_VALUE;
        }
    }
}
