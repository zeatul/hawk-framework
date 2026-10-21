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
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public abstract class ReflectHelper {

    /**
     * Constructs an object instance (no arguments).
     *
     * @param <T>   the type of the object to construct
     * @param clazz the class of the object to construct
     * @return the object instance
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T instance(@Nonnull Class<T> clazz) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            MethodHandle mh = lookup.findConstructor(clazz, MethodType.methodType(void.class));
            return (T) mh.invokeWithArguments();
        } catch (java.lang.Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs an object instance.
     * <p>Using this method requires being able to precisely infer the corresponding constructor's parameter types from the argument values.</p>
     * <p>For null arguments, Object.class is used as the type, which may lead to not finding an exact constructor. Using {@link #instance(Class, Class[], Object...)} to explicitly specify parameter types is recommended.</p>
     *
     * @param <T>   the type of the object to construct
     * @param clazz the class of the object to construct
     * @param args  the constructor arguments passed in
     * @return the object instance
     * @throws RuntimeException if no matching constructor is found
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T instance(@Nonnull Class<T> clazz, Object... args) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            if (args == null || args.length == 0) {
                MethodHandle mh = lookup.findConstructor(clazz, MethodType.methodType(void.class));
                return (T) mh.invokeWithArguments();
            } else {
                Class<?>[] paramTypes = new Class<?>[args.length];
                for (int i = 0; i < args.length; i++) {
                    paramTypes[i] = args[i] != null ? args[i].getClass() : Object.class;
                }
                MethodHandle mh = lookup.findConstructor(clazz, MethodType.methodType(void.class, paramTypes));
                return (T) mh.invokeWithArguments(args);
            }
        } catch (java.lang.Throwable e) {
            // Try using reflection as a fallback, supporting broader type matching
            try {
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                for (Constructor<?> ctor : constructors) {
                    Class<?>[] paramTypes = ctor.getParameterTypes();
                    if (paramTypes.length != (args == null ? 0 : args.length)) continue;
                    boolean match = true;
                    for (int i = 0; i < paramTypes.length; i++) {
                        Object arg = args[i];
                        if (arg != null && !paramTypes[i].isAssignableFrom(arg.getClass())) {
                            match = false;
                            break;
                        }
                    }
                    if (match) {
                        ctor.setAccessible(true);
                        return (T) ctor.newInstance(args);
                    }
                }
            } catch (Exception ignored) {
            }
            throw new RuntimeException("No matching constructor found for class " + clazz.getName() + " with given arguments", e);
        }
    }

    /**
     * Constructs an object instance.
     * <p>The argument types passed in must exactly match the constructor's parameter types</p>
     *
     * @param <T>      the type of the object to construct
     * @param clazz    the class of the object to construct
     * @param argTypes the array of constructor parameter types
     * @param args     the constructor arguments passed in
     * @return the object instance
     */
    @Nonnull
    @SuppressWarnings("unchecked")
    public static <T> T instance(@Nonnull Class<T> clazz, @Nonnull Class<?>[] argTypes, Object... args) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();

            if (args == null || args.length == 0) {
                MethodHandle mh = lookup.findConstructor(clazz, MethodType.methodType(void.class));
                return (T) mh.invokeWithArguments();
            }

            MethodType mt = MethodType.methodType(void.class, argTypes);
            MethodHandle mh = lookup.findConstructor(clazz, mt);

            return (T) mh.invokeWithArguments(args);

        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the corresponding Getter based on a Field.
     *
     * @param field the required field.
     * @return a Getter instance.
     */
    public static @Nonnull Method getGetter(@Nonnull Field field) {
        String getterName = "get" + StringHelper.capitalize(field.getName());
        Method method = findMethod(field.getDeclaringClass(), getterName);
        if (method == null && (field.getType() == boolean.class || field.getType() == Boolean.class)) {
            method = findMethod(field.getDeclaringClass(), "is" + StringHelper.capitalize(field.getName()));
        }
        if (method == null) throw new RuntimeException("No getter found for field " + field.getName() + " in " + field.getDeclaringClass().getName());
        return method;
    }

    /**
     * Gets the corresponding Getter based on a Field.
     *
     * @param objectClass the class, must not be null
     * @param fieldName   the field name, must not be null
     * @return a Method instance
     */
    public static @Nonnull Method getGetter(@Nonnull Class<?> objectClass, @Nonnull String fieldName) {
        String getterName = "get" + StringHelper.capitalize(fieldName);
        Method method = findMethod(objectClass, getterName);
        if (method == null) {
            method = findMethod(objectClass, "is" + StringHelper.capitalize(fieldName));
        }
        if (method == null) throw new RuntimeException("No getter found for field " + fieldName + " in " + objectClass.getName());
        return method;
    }

    /**
     * Gets the corresponding Setter based on a Field.
     *
     * @param field the required Setter.
     * @return a setter instance.
     */
    public static @Nonnull Method getSetter(@Nonnull Field field) {
        String setterName = "set" + StringHelper.capitalize(field.getName());
        Method method = findMethod(field.getDeclaringClass(), setterName, field.getType());
        if (method == null) throw new RuntimeException("No setter found for field " + field.getName() + " in " + field.getDeclaringClass().getName());
        return method;
    }

    /**
     * Gets the corresponding Setter based on a Field.
     *
     * @param objectClass the class, must not be null
     * @param fieldName   the field name, must not be null
     * @param fieldType   the field type, must not be null
     * @return a Method instance
     */
    public static @Nonnull Method getSetter(@Nonnull Class<?> objectClass, @Nonnull String fieldName, @Nonnull Class<?> fieldType) {
        String setterName = "set" + StringHelper.capitalize(fieldName);
        Method method = findMethod(objectClass, setterName, fieldType);
        if (method == null) throw new RuntimeException("No setter found for field " + fieldName + " in " + objectClass.getName());
        return method;
    }

    private static Method findMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            try {
                return c.getDeclaredMethod(name, parameterTypes);
            } catch (NoSuchMethodException ignored) {
            }
        }
        return null;
    }
}
