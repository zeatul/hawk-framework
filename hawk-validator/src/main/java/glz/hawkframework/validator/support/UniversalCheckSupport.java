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

import glz.hawkframework.validator.constraints.UniversalCheck;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>Supporting UniversalCheckValidator and caching {@link UniversalCheck}.</p>
 * <p><b>Naming convention:</b> every {@code checkerName} must use a fully-qualified name (for example {@code "com.example.order.OrderAgeCheck"}),
 * to avoid conflicts when validators with the same name are registered by different modules/applications.</p>
 *
 * @author Zhang Peng
 */
public class UniversalCheckSupport {

    private final static Map<String, Function<?, Boolean>> checkerMap = new ConcurrentHashMap<>();
    private final static Map<String, Function<?, Map<String, Object>>> messageParameterSupplierMap = new ConcurrentHashMap<>();

    /**
     * Gets the validation method.
     *
     * @param checkerName the validation method name
     * @return the validation method
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> Function<T, Boolean> loadChecker(String checkerName) {
        Function<T, Boolean> checker = (Function<T, Boolean>) checkerMap.get(argNotNull(checkerName, "checkMethod"));
        if (checker == null) throw new IllegalStateException(String.format("Can't find check method named %s", checkerName));
        return checker;
    }

    /**
     * Finds the [message template parameter supplier] method, used to obtain the parameters required by the message template when validation fails.
     *
     * @param checkerName the validation method name
     * @return the message parameter supplier
     */
    @Nullable
    @SuppressWarnings("unchecked")
    public static <T> Function<T, Map<String, Object>> findMessageParameterSupplier(@Nonnull String checkerName) {
        return (Function<T, Map<String, Object>>) messageParameterSupplierMap.get(argNotNull(checkerName, "checkerName"));
    }

    /**
     * Adds a validation method.
     *
     * @param checkerName the validation method name; a fully-qualified name must be used (for example {@code "com.example.order.OrderAgeCheck"})
     * @param function    the validation method
     * @throws IllegalArgumentException if the validation method name already exists
     */
    public static <T> void addChecker(String checkerName, Function<T, Boolean> function) {
        if (checkerMap.putIfAbsent(argNotNull(checkerName, "checkerName"), argNotNull(function, "function")) != null)
            throw new IllegalArgumentException(String.format("Duplicate check method named %s", checkerName));
    }

    /**
     * Adds a message parameter supplier.
     *
     * @param checkerName the validation method name; a fully-qualified name must be used (for example {@code "com.example.order.OrderAgeCheck"})
     * @param function    the message parameter supplier
     * @throws IllegalArgumentException if the validation method name already exists
     */
    public static <T> void addMessageParameterSupplier(String checkerName, Function<T, Map<String, Object>> function) {
        if (messageParameterSupplierMap.putIfAbsent(argNotNull(checkerName, "checkerName"), argNotNull(function, "function")) != null)
            throw new IllegalArgumentException(String.format("Duplicate check method named %s", checkerName));
    }

    /**
     * Clears all registered validators and message parameter suppliers.
     * <p>Used in test environments or hot-reload scenarios to prevent memory leaks.</p>
     */
    public static void clear() {
        checkerMap.clear();
        messageParameterSupplierMap.clear();
    }

    /**
     * Removes the specified validator.
     *
     * @param checkerName the validation method name
     * @return whether the removal succeeded
     */
    public static boolean removeChecker(String checkerName) {
        return checkerMap.remove(checkerName) != null;
    }

    /**
     * Removes the specified message parameter supplier.
     *
     * @param checkerName the validation method name
     * @return whether the removal succeeded
     */
    public static boolean removeMessageParameterSupplier(String checkerName) {
        return messageParameterSupplierMap.remove(checkerName) != null;
    }
}
