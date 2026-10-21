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

/**
 * This interface is responsible for
 * <p>A single-kind object query executor</p>
 *
 * @author Zhang Peng
 */
public interface CacheExecutor<K,V> {

    /**
     * Executes a query with caching.
     *
     * @param key    the cache key
     * @param loader the data loader
     * @return the query result
     */
     V execute(K key, Supplier<V> loader);

}
