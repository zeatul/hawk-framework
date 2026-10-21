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

import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>Uses a private sentinel class to avoid conflicts between user values and the notExist sentinel; null results are not cached, to avoid caching empty values</p>
 *
 * @author Zhang Peng
 */
public class DefaultCacheExecutor<K, V> implements CacheExecutor<K, V>, CacheManager<K> {

    // Private sentinel class, ensuring it will never be equal to a user value
    private static final class NotExistSentinel {
        private NotExistSentinel() {}
    }

    private final Cache<K, V> cache;
    // Store the sentinel instance using generic erasure, to avoid unchecked warnings
    @SuppressWarnings("unchecked")
    private final V notExist = (V) new NotExistSentinel();

    /**
     * Creates a DefaultCacheExecutor object.
     *
     * @param cache the cache implementation, must not be null
     */
    public DefaultCacheExecutor(Cache<K, V> cache) {
        this.cache = argNotNull(cache, "cache");
    }

    @Override
    public V execute(K key, Supplier<V> loader) {
        argNotNull(key, "key");
        argNotNull(loader, "loader");
        // Check the cache first, to avoid unnecessary computeIfAbsent calls
        V value = cache.get(key);
        if (value != null) {
            return value == notExist ? null : value;
        }
        // Atomic loading, to avoid repeatedly executing the loader on concurrent misses (cache stampede)
        // Use a private sentinel class to mark "not exists", to avoid conflicts between user return values and the sentinel
        // Note: null results are not cached, consistent with Cache.computeIfAbsent semantics
        value = cache.computeIfAbsent(key, k -> {
            V loaded = loader.get();
            return loaded == null ? notExist : loaded;
        });
        if (value == notExist) {
            // loader returned null, do not cache, remove the sentinel
            cache.evict(key);
            return null;
        }
        return value;
    }

    @Override
    public void evict(K key) {
        cache.evict(key);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    @Override
    public boolean exists(K key) {
        V value = cache.get(key);
        return value != null && value != notExist;
    }


}
