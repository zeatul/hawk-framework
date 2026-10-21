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

package glz.hawkframework.core.pojo;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class PojoParser {

    // Class-level field parsing cache, to avoid repeated reflection
    private static final ConcurrentHashMap<Class<?>, List<Field>> FIELDS_CACHE = new ConcurrentHashMap<>();

    public List<Field> getAllFields(Class<?> clazz) {
        argNotNull(clazz, "clazz");
        return FIELDS_CACHE.computeIfAbsent(clazz, this::parseFields);
    }

    private List<Field> parseFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> current = clazz;
        while (current != null && current != Object.class) {
            Field[] declared = current.getDeclaredFields();
            fields.addAll(Arrays.asList(declared));
            current = current.getSuperclass();
        }
        return fields;
    }

    /**
     * Clears the field cache for the specified class.
     *
     * @param clazz the class whose cache is to be cleared
     */
    public static void clearCache(Class<?> clazz) {
        FIELDS_CACHE.remove(clazz);
    }

    /**
     * Clears all field caches.
     */
    public static void clearAllCache() {
        FIELDS_CACHE.clear();
    }
}
