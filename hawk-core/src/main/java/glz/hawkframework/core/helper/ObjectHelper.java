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

package glz.hawkframework.core.helper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * Miscellaneous {@link Object} utility methods.
 * <p>Mainly for internal use within the framework.</p>
 *
 * @author Zhang Peng
 */
public abstract class ObjectHelper {

    private final static Map<String, Function<Object, Object>> GETTER_MAP = new ConcurrentHashMap<>();
    private final static Map<String, BiConsumer<Object, Object>> SETTER_MAP = new ConcurrentHashMap<>();

    /**
     * Determine if the given objects are equal, returning {@code true} if
     * both are {@code null} or {@code false} if only one is {@code null}.
     * <p>
     * Compares arrays with {@code Arrays.equals}, performing an equality
     * check based on the array elements rather than the array reference.
     * </p>
     *
     * @param o1 first Object to compare
     * @param o2 second Object to compare
     * @return whether the given objects are equal
     * @see Object#equals(Object)
     * @see Arrays#equals
     */
    public static boolean nullSafeEquals(@Nullable Object o1, @Nullable Object o2) {
        if (o1 == o2) {
            return true;
        }
        if (o1 == null || o2 == null) {
            return false;
        }
        if (o1.equals(o2)) {
            return true;
        }
        if (o1.getClass().isArray() && o2.getClass().isArray()) {
            return arrayEquals(o1, o2);
        }
        return false;
    }

    /**
     * Compare the given arrays with {@code Arrays.equals}, performing an equality
     * check based on the array elements rather than the array reference.
     *
     * @param o1 first array to compare
     * @param o2 second array to compare
     * @return whether the given objects are equal
     * @see #nullSafeEquals(Object, Object)
     * @see Arrays#equals
     */
    private static boolean arrayEquals(Object o1, Object o2) {
        if (o1 instanceof Object[] && o2 instanceof Object[]) {
            return Arrays.equals((Object[]) o1, (Object[]) o2);
        }
        if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
            return Arrays.equals((boolean[]) o1, (boolean[]) o2);
        }
        if (o1 instanceof byte[] && o2 instanceof byte[]) {
            return Arrays.equals((byte[]) o1, (byte[]) o2);
        }
        if (o1 instanceof char[] && o2 instanceof char[]) {
            return Arrays.equals((char[]) o1, (char[]) o2);
        }
        if (o1 instanceof double[] && o2 instanceof double[]) {
            return Arrays.equals((double[]) o1, (double[]) o2);
        }
        if (o1 instanceof float[] && o2 instanceof float[]) {
            return Arrays.equals((float[]) o1, (float[]) o2);
        }
        if (o1 instanceof int[] && o2 instanceof int[]) {
            return Arrays.equals((int[]) o1, (int[]) o2);
        }
        if (o1 instanceof long[] && o2 instanceof long[]) {
            return Arrays.equals((long[]) o1, (long[]) o2);
        }
        if (o1 instanceof short[] && o2 instanceof short[]) {
            return Arrays.equals((short[]) o1, (short[]) o2);
        }
        return false;
    }

    /**
     * Determine whether the given object is empty.
     * <p>
     * This method supports the following object types.
     * <ul>
     * <li>{@code Optional}: considered empty if {@link Optional#empty()}</li>
     * <li>{@code Array}: considered empty if its length is zero</li>
     * <li>{@link CharSequence}: considered empty if its length is zero</li>
     * <li>{@link Collection}: delegates to {@link Collection#isEmpty()}</li>
     * <li>{@link Map}: delegates to {@link Map#isEmpty()}</li>
     * </ul>
     * <p>
     * If the given object is non-null and not one of the aforementioned
     * supported types, this method returns {@code false}.
     *
     * @param obj the object to check
     * @return {@code true} if the object is {@code null} or <em>empty</em>
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(@Nullable Object obj) {
        if (obj == null) {
            return true;
        }

        if (obj instanceof Optional) {
            return !((Optional) obj).isPresent();
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map) obj).isEmpty();
        }
        return false;
    }

    /**
     * Determine whether the given object is empty.
     *
     * @param obj the object to check
     * @return {@code true} if the object is not <em>empty</em>
     * @see #isEmpty(Object)
     */
    public static boolean isNotEmpty(@Nullable Object obj) {
        return !isEmpty(obj);
    }

    /**
     * Determine whether the given array is empty:
     * i.e. {@code null} or of zero length.
     *
     * @param array the array to check
     * @return {@code true} if the array is {@code null} or <em>empty</em>
     * @see #isEmpty(Object)
     */
    public static boolean isEmpty(@Nullable Object[] array) {
        return (array == null || array.length == 0);
    }

    /**
     * Determine whether the given array is not empty.
     *
     * @param array the array to check
     * @return {@code true} if the array is not <em>empty</em>
     * @see #isEmpty(Object[])
     */
    public static boolean isNotEmpty(@Nullable Object[] array) {
        return !isEmpty(array);
    }

    /**
     * Gets an object's field value.
     *
     * @param object    the object instance, must not be null
     * @param fieldName the field name, must not be null
     * @param fieldType the field value type, must not be null
     * @param <T>       the field value class type
     * @return the field value
     */
    @SuppressWarnings("unchecked")
    public static <T> @Nullable T getFieldValue(@Nonnull Object object, @Nonnull String fieldName, @Nonnull Class<T> fieldType) {
        return (T) getFieldValue(object, fieldName);
    }

    /**
     * Gets an object's field value.
     *
     * @param object    the object instance, must not be null
     * @param fieldName the field name, must not be null
     * @return the field value
     */
    public static @Nullable Object getFieldValue(@Nonnull Object object, @Nonnull String fieldName) {
        final Class<?> objectClass = object.getClass();
        final String key = cacheKey(objectClass, fieldName);
        Function<Object, Object> getter = GETTER_MAP.computeIfAbsent(key, k -> LambdaHelper.createGetter(ReflectHelper.getGetter(objectClass, fieldName)));
        return getter.apply(object);
    }

    /**
     * Sets an object's field value.
     *
     * @param object    the object instance, must not be null
     * @param fieldName the field name, must not be null
     * @param newValue  the new field value; if empty, the field value is set to {@code null}
     * @param valueType the field value type, must not be null
     */
    public static void setFieldValue(@Nonnull Object object, @Nonnull String fieldName, @Nullable Object newValue, @Nonnull Class<?> valueType) {
        final Class<?> objectClass = object.getClass();
        final String key = cacheKey(objectClass, fieldName, valueType);
        BiConsumer<Object, Object> setter = SETTER_MAP.computeIfAbsent(key, k -> LambdaHelper.createSetter(ReflectHelper.getSetter(objectClass, fieldName, valueType)));
        setter.accept(object, newValue);
    }

    /**
     * Sets an object's field value.
     *
     * @param object    the object instance, must not be null
     * @param fieldName the field name, must not be null
     * @param newValue  the new field value, must not be null
     */
    public static void setFieldValue(@Nonnull Object object, @Nonnull String fieldName, @Nonnull Object newValue) {
        setFieldValue(object, fieldName, newValue, declaredFieldType(object.getClass(), fieldName, newValue));
    }

    private static String cacheKey(Class<?> objectClass, String fieldName) {
        return objectClass.getName() + ":" + fieldName;
    }

    /**
     * The cache key includes the field type, to distinguish overloaded setters.
     */
    private static String cacheKey(Class<?> objectClass, String fieldName, Class<?> valueType) {
        return objectClass.getName() + ":" + fieldName + ":" + valueType.getName();
    }

    private static Class<?> declaredFieldType(Class<?> objectClass, String fieldName, Object newValue) {
        for (Class<?> c = objectClass; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredField(fieldName).getType();
            } catch (NoSuchFieldException ignored) {
            }
        }
        return newValue.getClass();
    }
}
