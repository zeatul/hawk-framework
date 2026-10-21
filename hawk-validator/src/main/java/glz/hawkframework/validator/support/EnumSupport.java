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

package glz.hawkframework.validator.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class EnumSupport {

    private static final Function<?, ?> NONE = x -> null;
    private static final Function<String, ?> NONE_FOR_NAME = x -> null;

    private final static Map<String, Function<?, ?>> valueParserMap = new ConcurrentHashMap<>();
    private final static Map<String, Function<String, ?>> nameParserMap = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>, V> T fromValue(Class<T> enumClass, V value) {
        if (value == null) return null;
        String key = enumClass.getName();
        if (valueParserMap.containsKey(key)) {
            Function<V, T> function = (Function<V, T>) valueParserMap.get(key);
            if (function == NONE) throw new IllegalArgumentException("Found no parser for " + key);
            return function.apply(value);
        } else {
            Method method = findMethod(enumClass, "fromValue", value.getClass());
            if (method == null) {
                valueParserMap.put(key, NONE);
                throw new IllegalArgumentException("Found no parser for " + key);
            }
            Function<V, T> function = v -> {
                try {
                    return (T) (method.invoke(null, v));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            valueParserMap.put(key, function);
            return function.apply(value);
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T fromName(Class<T> enumClass, String name) {
        if (name == null) return null;
        String key = enumClass.getName();
        if (nameParserMap.containsKey(key)) {
            Function<String, T> function = (Function<String, T>) nameParserMap.get(key);
            if (function == NONE_FOR_NAME) throw new IllegalArgumentException("Found no parser for " + key);
            return function.apply(name);
        } else {
            Method method = findMethod(enumClass, "fromName", String.class);
            if (method == null) {
                nameParserMap.put(key, NONE_FOR_NAME);
                throw new IllegalArgumentException("Found no parser for " + key);
            }
            Function<String, T> function = (String v) -> {
                try {
                    return (T) (method.invoke(null, v));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            nameParserMap.put(key, function);
            return function.apply(name);
        }
    }

    private static Method findMethod(Class<?> enumClass, String name, Class<?> parameterType) {
        for (Class<?> c = parameterType; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return enumClass.getDeclaredMethod(name, c);
            } catch (NoSuchMethodException ignored) {
            }
        }
        try {
            return enumClass.getDeclaredMethod(name, Object.class);
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    public static <T extends Enum<T>> void addNameParser(Class<T> enumClass, Function<String, T> parser) {
        nameParserMap.put(enumClass.getName(), parser);
    }

    public static <T extends Enum<T>, V> void addValueParser(Class<T> enumClass, Function<V, T> parser) {
        valueParserMap.put(enumClass.getName(), parser);
    }

    /**
     * Clears all registered parsers.
     * <p>Used in test environments or hot-reload scenarios to prevent memory leaks.</p>
     */
    public static void clear() {
        valueParserMap.clear();
        nameParserMap.clear();
    }

    /**
     * Removes the parser of the specified enum.
     *
     * @param enumClass the enum class
     * @return whether the removal succeeded
     */
    public static boolean removeParser(Class<? extends Enum<?>> enumClass) {
        String key = enumClass.getName();
        boolean removed1 = valueParserMap.remove(key) != null;
        boolean removed2 = nameParserMap.remove(key) != null;
        return removed1 || removed2;
    }
}