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
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Miscellaneous argument check methods.
 * <p>Mainly for internal use within the framework.</p>
 *
 * @author Hawk
 */
public class ArgumentSupport {

    public static String NOT_NULL = "The parameter must not be null.";
    public static String NOT_NULL_WITH_PARAMETER = "The parameter[%s] must not be null.";
    public static String NOT_EMPTY = "The parameter must not be empty.";
    public static String NOT_EMPTY_WITH_PARAMETER = "The parameter[%s] must not be empty.";
    public static String NOT_BLANK = "The parameter must not be blank.";
    public static String NOT_BLANK_WITH_PARAMETER = "The parameter[%s] must not be blank.";
    public static String GREATER_THAN = "The parameter[%s] must not be greater than %s.";
    public static String GREATER_THAN_OR_EQUAL_TO = "The parameter[%s] must not be greater than or equal to %s.";

    /**
     * Throws an {@link IllegalArgumentException} if the input parameter 'arg' is {@code null},
     * otherwise returns it directly.
     *
     * @param <T> the type of input parameter 'arg'
     * @param arg the parameter to check.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static <T> T argNotNull(T arg) throws IllegalArgumentException {
        if (arg == null) {
            throw new IllegalArgumentException(NOT_NULL);
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg' is {@code null},
     * otherwise returns it directly.
     *
     * @param <T>     the type of input parameter 'arg'
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static <T> T argNotNull(T arg, @Nonnull String argName) throws IllegalArgumentException {
        if (arg == null) {
            throw new IllegalArgumentException(String.format(NOT_NULL_WITH_PARAMETER, argName));
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg'
     * is {@code null} or its element is {@code null}, otherwise returns it directly.
     *
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @param <T>     the type of the input parameter
     * @return the input parameter 'arg' directly
     */
    public static <T extends Iterable<?>> T argNoNullElement(T arg, @Nonnull String argName) {
        int index = -1;
        for (Object object : argNotNull(arg, argName)) {
            if (object == null) {
                throw new IllegalArgumentException(String.format("The %dth element in %s is null.", ++index, argName));
            }
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg'
     * is {@code null} or its element is {@code null}, otherwise returns it directly.
     *
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @param <T>     the type of the input parameter
     * @return the input parameter 'arg' directly
     */
    public static <T> T[] argNoNullElement(T[] arg, @Nonnull String argName) {
        int index = -1;
        for (Object object : argNotNull(arg, argName)) {
            if (object == null) {
                throw new IllegalArgumentException(String.format("The %dth element in %s is null.", ++index, argName));
            }
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg'
     * is {@code null} or its element is {@code null}, otherwise returns it directly.
     *
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @param <T>     the type of the input parameter
     * @return the input parameter 'arg' directly
     */
    public static <T extends Iterable<String>> T argNoBlankElement(T arg, @Nonnull String argName) {
        int index = -1;
        for (String object : argNotNull(arg, argName)) {
            if (StringHelper.isBlank(object)) {
                throw new IllegalArgumentException(String.format("The %dth element is blank in %s", ++index, argName));
            }
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} with the supplied message if the input parameter 'arg' is {@code null},
     * otherwise returns it directly.
     *
     * @param <T>             the type of input parameter 'arg'
     * @param arg             the parameter to check.
     * @param messageSupplier the supplier to provide the message for the exception.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static <T> T argNotNull(T arg, @Nonnull Supplier<String> messageSupplier) throws IllegalArgumentException {
        if (arg == null) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return arg;
    }

    /**
     * Ensure the element in the {@code index} position of {@code containerName} is not null.
     *
     * @param element       the element to check
     * @param index         the position of element in the container
     * @param containerName the name of container
     * @param <T>           the type of the element
     * @return the input element
     */
    public static <T> T argElementNotNull(T element, int index, String containerName) {
        if (element == null) {
            throw new IllegalArgumentException(String.format("The %dth element is null in %s", index, containerName));
        }
        return element;
    }

    /**
     * Ensure the element in the {@code index} position of {@code containerName} is not blank.
     *
     * @param element       the element to check
     * @param index         the position of element in the container
     * @param containerName the name of container
     * @return the input element
     */
    public static String argElementNotBlank(String element, int index, String containerName) {
        if (StringHelper.isBlank(element)) {
            throw new IllegalArgumentException(String.format("The %dth element is null in %s", index, containerName));
        }
        return element;
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input parameter 'arg' is empty,
     * otherwise returns it directly.
     * <p>
     * The method supports Array, {@link Collection}, {@link Map}, {@link Optional}.
     * The empty object is null or contains nothing.
     *
     * @param <T> the type of input parameter 'arg'
     * @param arg the parameter to check.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static <T> T argNotEmpty(T arg) {
        if (ObjectHelper.isEmpty(arg)) {
            throw new IllegalArgumentException(NOT_EMPTY);
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg' is empty,
     * otherwise returns it directly.
     * <p>
     * The method supports Array, {@link Collection}, {@link Map}, {@link Optional}.
     * The empty object is null or contains nothing.
     *
     * @param <T>     the type of input parameter 'arg'
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static <T> T argNotEmpty(T arg, @Nonnull String argName) throws IllegalArgumentException {
        if (ObjectHelper.isEmpty(arg)) {
            throw new IllegalArgumentException(String.format(NOT_EMPTY_WITH_PARAMETER, argName));
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg' is empty or has null element,
     * otherwise returns it directly.
     * <p>
     * The method supports Array, {@link Collection}, {@link Map}, {@link Optional}.
     * The empty object is null or contains nothing.
     *
     * @param <T>     the type of input parameter 'arg'
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @return the input parameter 'arg' directly
     */
    public static <T extends Iterable<?>> T argNotEmptyAndNoNulElement(T arg, @Nonnull String argName) throws IllegalArgumentException {
        argNotEmpty(arg, argName);
        argNoNullElement(arg, argName);
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg' is empty or has null element,
     * otherwise returns it directly.
     * <p>
     * The method supports Array, {@link Collection}, {@link Map}, {@link Optional}.
     * The empty object is null or contains nothing.
     *
     * @param <T>     the type of input parameter 'arg'
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @return the input parameter 'arg' directly
     */
    public static <T> T[] argNotEmptyAndNoNulElement(T[] arg, @Nonnull String argName) throws IllegalArgumentException {
        argNotEmpty(arg, argName);
        argNoNullElement(arg, argName);
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} with the supplied message if the input parameter 'arg' is empty,
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
    public static <T> T argNotEmpty(T arg, @Nonnull Supplier<String> messageSupplier) throws IllegalArgumentException {
        if (ObjectHelper.isEmpty(arg)) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input parameter 'arg' is blank,
     * otherwise returns it directly.
     * <p>
     * The blank string is null, containing nothing or only containing whitespace characters.
     *
     * @param arg the parameter to check.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static String argNotBlank(String arg) throws IllegalArgumentException {
        if (ObjectHelper.isEmpty(arg)) {
            throw new IllegalArgumentException(NOT_BLANK);
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} containing the input argument name if the input parameter 'arg' is blank,
     * otherwise returns it directly.
     * <p>
     * The blank string is null, containing nothing or only containing whitespace characters.
     *
     * @param arg     the parameter to check.
     * @param argName the name of input parameter 'arg'
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static String argNotBlank(String arg, @Nonnull String argName) throws IllegalArgumentException {
        if (StringHelper.isBlank(arg)) {
            throw new IllegalArgumentException(String.format(NOT_BLANK_WITH_PARAMETER, argName));
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} with the supplied message if the input parameter 'arg' is blank,
     * otherwise returns it directly.
     * <p>
     * The blank string is null, containing nothing or only containing whitespace characters.
     *
     * @param arg             the parameter to check.
     * @param messageSupplier the supplier to provide the message for the exception.
     * @return the input parameter 'arg' directly
     */
    @Nonnull
    public static String argNotBlank(String arg, @Nonnull Supplier<String> messageSupplier) throws IllegalArgumentException {
        if (StringHelper.isBlank(arg)) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} if the input parameter 'arg' failed to pass the check of the input parameter 'argChecker',
     * otherwise returns it directly.
     *
     * @param arg             the parameter to check.
     * @param argChecker      the function to check the input parameter 'arg'
     * @param messageSupplier the supplier to provide the message for the exception.
     * @return the input parameter 'arg' directly
     */
    public static <T> T argument(@Nonnull T arg, @Nonnull Function<T, Boolean> argChecker, @Nonnull Function<T, String> messageSupplier) throws IllegalArgumentException {
        Boolean b = argChecker.apply(argNotNull(arg));
        if (b == null || !b) {
            throw new IllegalArgumentException(messageSupplier.apply(arg));
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} if any element in the input parameter 'arg'
     * failed to pass the check of the input parameter 'argChecker', otherwise returns it directly.
     *
     * @param arg               the parameter to check
     * @param argElementChecker the function to check the element int the input parameter 'arg'
     * @param messageSupplier   the supplier to provide the message for the exception.
     * @param <K>               the type of element
     * @param <T>               the type of arg
     * @return the input parameter 'arg' directly
     */
    public static <K, T extends Iterable<K>> T argumentElement(@Nonnull T arg, @Nonnull Function<K, Boolean> argElementChecker, @Nonnull BiFunction<K, Integer, String> messageSupplier) throws IllegalArgumentException {
        int index = -1;
        for (K k : argNotNull(arg)) {
            Boolean b = argElementChecker.apply(k);
            if (b == null || !b) {
                throw new IllegalArgumentException(messageSupplier.apply(k, index));
            }
        }
        return arg;
    }

    /**
     * Throws an {@link IllegalArgumentException} if the value of {@code booleanExpression} is {@code false}.
     *
     * @param booleanExpression the required boolean expression.
     * @param messageSupplier   the supplier to provide the message for the exception.
     */
    public static void argTrue(boolean booleanExpression, @Nonnull Supplier<String> messageSupplier) throws IllegalArgumentException {
        if (!booleanExpression) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the value of {@code booleanSupplier} is {@code null} or {@code false}.
     *
     * @param booleanSupplier the supplier to provide boolean value.
     * @param messageSupplier the supplier to provide the message for the exception.
     */
    public static void argTrue(@Nonnull Supplier<Boolean> booleanSupplier, @Nonnull Supplier<String> messageSupplier) throws IllegalArgumentException {
        if (booleanSupplier.get() == null || !booleanSupplier.get()) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the value of {@code booleanExpression} is {@code true}.
     *
     * @param booleanExpression the supplier to provide boolean value.
     * @param messageSupplier   the supplier to provide the message for the exception.
     */
    public static void argFalse(boolean booleanExpression, @Nonnull Supplier<String> messageSupplier) throws IllegalArgumentException {
        if (booleanExpression) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

    /**
     * Throws an {@link IllegalArgumentException} if the value of {@code booleanSupplier} is {@code true}.
     *
     * @param booleanSupplier the supplier to provide boolean value.
     * @param messageSupplier the supplier to provide the message for the exception.
     */
    public static void argFalse(@Nonnull Supplier<Boolean> booleanSupplier, @Nonnull Supplier<String> messageSupplier) throws IllegalArgumentException {
        if (booleanSupplier.get() != null && booleanSupplier.get()) {
            throw new IllegalArgumentException(messageSupplier.get());
        }
    }

}
