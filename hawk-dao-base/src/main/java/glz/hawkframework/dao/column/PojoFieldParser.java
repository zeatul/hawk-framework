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

package glz.hawkframework.dao.column;

import glz.hawkframework.core.helper.LambdaHelper;
import glz.hawkframework.core.helper.ReflectHelper;
import glz.hawkframework.core.helper.StringHelper;
import glz.hawkframework.dao.column.annotation.ColumnFunction;
import org.springframework.core.annotation.MergedAnnotations;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
@SuppressWarnings("unchecked")
public class PojoFieldParser<K> {

    private final Class<K> pojoClass;

    // Class-level parse result cache, to avoid repeated reflection parsing
    private static final ConcurrentHashMap<Class<?>, Object> PARSE_CACHE = new ConcurrentHashMap<>();

    public PojoFieldParser(@Nonnull Class<K> pojoClass) {
        this.pojoClass = argNotNull(pojoClass, "pojoClass");
    }

    @Nonnull
    public <V, Z extends Annotation> Map<Class<? extends Annotation>, List<FieldDescriptor<K, V, Z>>> parse() {
        // Get from cache or parse and cache
        @SuppressWarnings("unchecked")
        Map<Class<? extends Annotation>, List<FieldDescriptor<K, V, Z>>> cached = 
            (Map<Class<? extends Annotation>, List<FieldDescriptor<K, V, Z>>>) PARSE_CACHE.computeIfAbsent(pojoClass, this::doParse);
        return cached;
    }

    /**
     * Actually performs the parsing logic.
     */
    @Nonnull
    private <V, Z extends Annotation> Map<Class<? extends Annotation>, List<FieldDescriptor<K, V, Z>>> doParse(Class<?> clazz) {
        Map<Class<? extends Annotation>, List<FieldDescriptor<K, V, Z>>> map = new HashMap<>();
        Arrays.stream(clazz.getDeclaredFields()).forEach(field ->
            Arrays.stream(field.getAnnotations()).forEach(annotation -> {
                MergedAnnotations mergedAnnotations = MergedAnnotations.from(annotation);
                if (!mergedAnnotations.isPresent(ColumnFunction.class)) {
                    return;
                }
                Method updatedGetter = getUpdatedGetter(field);
                FieldDescriptor<K, V, Z> fieldDescriptor = new FieldDescriptor<>(
                    field.getName(),
                    (Z) annotation,
                    LambdaHelper.createGetter(ReflectHelper.getGetter(field)),
                    updatedGetter == null ? null : LambdaHelper.createGetter(updatedGetter),
                    LambdaHelper.createSetter(ReflectHelper.getSetter(field))
                );
                map.computeIfAbsent(annotation.annotationType(), key -> new ArrayList<>()).add(fieldDescriptor);
            }));
        return map;
    }

    /**
     * Clears the parse cache of the specified class, for testing or hot reload scenarios.
     *
     * @param clazz the class whose cache is to be cleared, must not be null
     */
    public static void clearCache(@Nonnull Class<?> clazz) {
        PARSE_CACHE.remove(argNotNull(clazz, "clazz"));
    }

    /**
     * Clears all parse caches.
     */
    public static void clearAllCache() {
        PARSE_CACHE.clear();
    }

    protected Method getUpdatedGetter(Field field) {
        try {
            return pojoClass.getDeclaredMethod(getUpdatedGetterName(field.getName()));
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

    protected String getUpdatedGetterName(String fieldName) {
        return "is" + StringHelper.capitalize(fieldName) + "Updated";
    }
}
