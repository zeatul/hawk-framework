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
 * <p></p>
 *
 * @author Zhang Peng
 */
public class DefaultCacheExecutor<K, V> implements CacheExecutor<K, V>, CacheManager<K> {
    private final V notExist;

    private final Cache<K, V> cache;

    /**
     * 创建DefaultCacheExecutor对象
     *
     * @param cache           缓存
     * @param notExist        一个对象，用来指代查不到数据
     */
    public DefaultCacheExecutor(Cache<K, V> cache, V notExist) {
        this.cache = argNotNull(cache, "cache");
        this.notExist = argNotNull(notExist, "notExist");
    }

    @Override
    public V execute(K key, Supplier<V> loader) {
        V value = cache.get(key);
        if (value != null) {
            if (value.equals(notExist)) return null;
            return value;
        }
        value = loader.get();
        if (value != null) {
            cache.put(key, value);
            return value;
        } else {
            cache.put(key, notExist);
            return null;
        }
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
        return value != null && !value.equals(notExist);
    }


}
