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
import java.util.concurrent.TimeUnit;

/**
 * This interface is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public interface Cache<K, V> extends CacheManager<K>{

    /**-
     * 获取缓存的值
     *
     * @param key 缓存key
     * @return 缓存值
     */
    @Nullable
    V get(@Nonnull K key);

    /**
     * 放入缓存值
     *
     * @param key   缓存key，不可为空
     * @param value 缓存值，不可为空
     */
    void put(@Nonnull K key, @Nonnull V value);



}
