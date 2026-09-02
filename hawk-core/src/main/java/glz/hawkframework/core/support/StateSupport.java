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

package glz.hawkframework.core.support;

import glz.hawkframework.core.helper.ObjectHelper;
import glz.hawkframework.core.helper.StringHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Miscellaneous state check methods.
 * <p>Mainly for internal use within the framework.</p>
 *
 * @author Hawk
 */
public class StateSupport {


    /**
     * Throws an {@link IllegalStateException} with the supplied message if the input parameter 'arg' is {@code null},
     * otherwise returns it directly.
     *
     * @param <T>             the type of input parameter 'arg'
     * @param arg             the parameter to check.
     * @param messageSupplier the supplier to provide the message for the exception.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static <T> T stateNotNull(@Nullable T arg, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (arg == null) {
            throw new IllegalStateException(messageSupplier.get());
        }
        return arg;
    }


    /**
     * Throws an {@link IllegalStateException} with the supplied message if the input parameter 'arg' is empty,
     * otherwise returns it directly.
     * <p>
     * The method supports Array, {@link Collection}, {@link Map}, {@link Optional}.
     * The empty object is null or contains nothing.
     *
     * @param <T>             the type of input parameter 'arg'
     * @param arg             the parameter to check.
     * @param messageSupplier the supplier to provide the message for the exception.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static <T> T stateNotEmpty(@Nullable T arg, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (ObjectHelper.isEmpty(arg)) {
            throw new IllegalStateException(messageSupplier.get());
        }
        return arg;
    }


    /**
     * Throws an {@link IllegalStateException} with the supplied message if the input parameter 'arg' is blank,
     * otherwise returns it directly.
     * <p>
     * The blank string is null, containing nothing or only containing whitespace characters.
     *
     * @param arg             the parameter to check.
     * @param messageSupplier the supplier to provide the message for the exception.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static String stateNotBlank(@Nullable String arg, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (StringHelper.isBlank(arg)) {
            throw new IllegalStateException(messageSupplier.get());
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalStateException} if the input parameter 'arg' failed to pass the check of the input parameter 'argChecker',
     * otherwise returns it directly.
     *
     * @param arg             the parameter to check.
     * @param argChecker      the function for check the input parameter 'arg'
     * @param messageSupplier the supplier to provide the message for the exception.
     * @return the input parameter 'arg' directly
     */
    public static <T> T state(@Nullable T arg, @Nonnull Function<T, Boolean> argChecker, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (!argChecker.apply(arg)) {
            throw new IllegalStateException(messageSupplier.get());
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalStateException} if the value of {@code booleanExpression} is {@code false}.
     *
     * @param booleanExpression the required boolean expression.
     * @param messageSupplier   the supplier to provide the message for the exception.
     */
    public static void stateTrue(boolean booleanExpression, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (!booleanExpression) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }

    /**
     * Throws an {@link IllegalStateException} if the value of {@code booleanSupplier} is {@code null} or {@code false}.
     *
     * @param booleanSupplier the supplier to provide boolean value.
     * @param messageSupplier the supplier to provide the message for the exception.
     */
    public static void stateTrue(@Nonnull Supplier<Boolean> booleanSupplier, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (booleanSupplier.get() == null || !booleanSupplier.get()) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }

    /**
     * Throws an {@link IllegalStateException} if the value of {@code booleanExpression} is {@code true}.
     *
     * @param booleanExpression the supplier to provide boolean value.
     * @param messageSupplier   the supplier to provide the message for the exception.
     */
    public static void stateFalse(boolean booleanExpression, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (booleanExpression) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }

    /**
     * Throws an {@link IllegalStateException} if the value of {@code booleanSupplier} is {@code true}.
     *
     * @param booleanSupplier the required boolean expression.
     * @param messageSupplier the supplier to provide the message for the exception.
     */
    public static void stateFalse(@Nonnull Supplier<Boolean> booleanSupplier, @Nonnull Supplier<String> messageSupplier) throws IllegalStateException {
        if (booleanSupplier.get() != null && booleanSupplier.get()) {
            throw new IllegalStateException(messageSupplier.get());
        }
    }
}
