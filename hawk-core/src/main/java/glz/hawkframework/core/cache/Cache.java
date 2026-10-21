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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;

/**
 * This interface is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public interface Cache<K, V> extends CacheManager<K>{

    /**-
     * Gets the cached value.
     *
     * @param key the cache key
     * @return the cached value
     */
    @Nullable
    V get(@Nonnull K key);

    /**
     * Puts a value into the cache.
     *
     * @param key   the cache key, must not be null
     * @param value the cache value, must not be null
     */
    void put(@Nonnull K key, @Nonnull V value);

    /**
     * Atomically gets the cached value: if absent, computes it via {@code mappingFunction} and puts it into the cache.
     * <p>The entire method invocation is atomic, avoiding repeated loading on concurrent misses.</p>
     * <p>If {@code mappingFunction} returns {@code null}, nothing is written to the cache and {@code null} is returned.</p>
     * <p>The default implementation uses double-checked locking, which has concurrency performance issues; implementations are advised to override this method to use native concurrency support (such as Caffeine/Guava/ConcurrentHashMap).</p>
     *
     * @param key             the cache key, must not be null
     * @param mappingFunction the value loading function, must not be null
     * @return the value in the cache, may be {@code null}
     */
    @Nullable
    default V computeIfAbsent(@Nonnull K key, @Nonnull Function<? super K, ? extends V> mappingFunction) {
        V value = get(key);
        if (value != null) {
            return value;
        }
        // Use an instance lock instead of this, to avoid outside code holding the lock object
        return computeIfAbsentInternal(key, mappingFunction);
    }

    /**
     * Internal implementation; subclasses may override to provide better concurrency performance.
     */
    @Nullable
    default V computeIfAbsentInternal(@Nonnull K key, @Nonnull Function<? super K, ? extends V> mappingFunction) {
        synchronized (this) {
            V value = get(key);
            if (value == null) {
                value = mappingFunction.apply(key);
                if (value != null) {
                    put(key, value);
                }
            }
            return value;
        }
    }

}
