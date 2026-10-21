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

import com.google.common.cache.CacheBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.time.Duration;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>Implements Cache with Guava</p>
 * <p>Guava does not support controlling the expiration time of an individual key</p>
 *
 * @author Zhang Peng
 */
public class GuavaCache<K, V> implements Cache<K, V> {

    private final com.google.common.cache.Cache<K, V> cache;

    private GuavaCache(com.google.common.cache.Cache<K, V> cache) {
        this.cache = cache;
    }

    @Override
    @Nullable
    public V get(@Nonnull K key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void put(@Nonnull K key, @Nonnull V value) {
        cache.put(key, value);
    }

    @Override
    @Nullable
    public V computeIfAbsent(@Nonnull K key, @Nonnull Function<? super K, ? extends V> mappingFunction) {
        try {
            return cache.get(key, () -> mappingFunction.apply(key));
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            }
            if (cause instanceof Error) {
                throw (Error) cause;
            }
            throw new IllegalStateException(cause);
        }
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
        // Use asMap().containsKey to avoid triggering loading (if a CacheLoader is configured)
        return cache.asMap().containsKey(key);
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<>();
    }

    public static class Builder<K, V> {
        private int initialCapacity = 1000;
        private long maximumSize = 10000;
        private Duration expireAfterWrite = Duration.ofHours(2);
        private int concurrencyLevel = 16;

        public Builder<K, V> setInitialCapacity(int initialCapacity) {
            this.initialCapacity = initialCapacity;
            return this;
        }

        public Builder<K, V> setMaximumSize(long maximumSize) {
            this.maximumSize = maximumSize;
            return this;
        }

        public Builder<K, V> setExpireAfterWrite(long duration, TimeUnit unit) {
            this.expireAfterWrite = Duration.ofMillis(unit.toMillis(duration));
            return this;
        }

        public Builder<K, V> setExpireAfterWrite(Duration duration) {
            this.expireAfterWrite = duration;
            return this;
        }

        public Builder<K, V> setConcurrencyLevel(int concurrencyLevel) {
            this.concurrencyLevel = concurrencyLevel;
            return this;
        }


        public GuavaCache<K, V> build() {
            com.google.common.cache.Cache<K, V> cache = CacheBuilder.newBuilder()
                .concurrencyLevel(concurrencyLevel)
                .initialCapacity(initialCapacity)
                .maximumSize(maximumSize)
                .expireAfterWrite(expireAfterWrite)
                .build();
            return new GuavaCache<>(cache);
        }
    }
}
