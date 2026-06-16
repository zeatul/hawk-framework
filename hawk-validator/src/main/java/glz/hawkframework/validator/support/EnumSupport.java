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
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class EnumSupport {

    private final static Map<String, Function<?, ?>> valueParserMap = new HashMap<>();

    private final static Map<String, Function<String, ?>> nameParserMap = new HashMap<>();

    /**
     * 先从valueParserMap找，找不到，看enumClass有没有提供fromValue的静态方法，有则方法map，没有报错
     *
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>, V> T fromValue(Class<T> enumClass, V value) {
        if (value == null) return null;
        if (valueParserMap.containsKey(enumClass.getCanonicalName())) {
            Function<V, T> function = (Function<V, T>) valueParserMap.get(enumClass.getCanonicalName());
            if (function == null) throw new IllegalArgumentException("Found no parser for " + enumClass.getCanonicalName());
            return function.apply(value);
        } else {
            try {
                Method method = enumClass.getDeclaredMethod("fromValue", value.getClass());
                Function<V, T> function = v -> {
                    try {
                        return (T) (method.invoke(null, v));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
                valueParserMap.put(enumClass.getCanonicalName(), function);
                return function.apply(value);
            } catch (NoSuchMethodException ignored) {
                // 空值也要放入map，防止找不到默认方法，反复从反射获取
                valueParserMap.put(enumClass.getCanonicalName(), null);
                throw new IllegalArgumentException("Found no parser for " + enumClass.getCanonicalName());
            }
        }
    }

    /**
     * 先从nameParserMap找，找不到，看enumClass有没有提供fromName的静态方法，有则方法map，没有报错
     *
     */
    @SuppressWarnings("unchecked")
    public static <T extends Enum<T>> T fromName(Class<T> enumClass, String name) {
        if (name == null) return null;
        if (nameParserMap.containsKey(enumClass.getCanonicalName())) {
            Function<String, T> function = (Function<String, T>) nameParserMap.get(enumClass.getCanonicalName());
            if (function == null) throw new IllegalArgumentException("Found no parser for " + enumClass.getCanonicalName());
            return function.apply(name);
        } else {
            try {
                Method method = enumClass.getDeclaredMethod("fromName", name.getClass());
                Function<String, T> function = (String v) -> {
                    try {
                        return (T) (method.invoke(null, v));
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                };
                nameParserMap.put(enumClass.getCanonicalName(), function);
                return function.apply(name);
            } catch (NoSuchMethodException ignored) {
                // 空值也要放入map，防止找不到默认方法，反复从反射获取
                valueParserMap.put(enumClass.getCanonicalName(), null);
                throw new IllegalArgumentException("Found no parser for " + enumClass.getCanonicalName());
            }
        }
    }


    public static <T extends Enum<T>> void addNameParser(Class<T> enumClass, Function<String, T> parser) {
        nameParserMap.put(enumClass.getCanonicalName(), parser);
    }

    public static <T extends Enum<T>, V> void addValueParser(Class<T> enumClass, Function<V, T> parser) {
        valueParserMap.put(enumClass.getCanonicalName(), parser);
    }
}
