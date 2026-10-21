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

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public abstract class LambdaHelper {

    private final static MethodHandles.Lookup PUBLIC_LOOKUP = MethodHandles.publicLookup();

    public static <T> Supplier<T> instanceSupplier(Class<T> clazz) {
        return () -> {
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Convert the Getter method to a {@link Function} instance.
     * <p>JDK8: for public getters, try LambdaMetafactory, falling back to reflection on failure</p>
     *
     * @param <K> the type of object which holds the method.
     * @param <V> the type of return object.
     * @param m   the required method.
     * @return the Function instance
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Function<K, V> createGetter(Method m) {
        try {
            // Only valid for public methods that can be unreflect-ed with publicLookup
            MethodHandle mh = PUBLIC_LOOKUP.unreflect(m);
            // Normalize the type: (T) -> R
            mh = mh.asType(MethodType.methodType(m.getReturnType(), m.getDeclaringClass()));
            MethodType invokedType = MethodType.methodType(Function.class); // () -> Function
            MethodType samMethodType = MethodType.methodType(m.getReturnType(), m.getDeclaringClass()); // Function.apply(Object)->Object (erased)
            MethodType instantiatedMethodType = MethodType.methodType(m.getReturnType(), m.getDeclaringClass()); // (T)->R
            CallSite cs = LambdaMetafactory.metafactory(
                PUBLIC_LOOKUP,
                "apply",
                invokedType,
                samMethodType,
                mh,
                instantiatedMethodType
            );
            MethodHandle factory = cs.getTarget();
            return (Function<K, V>) factory.invoke();
        } catch (Throwable t) {
            // Fall back to reflection
            return reflectionGetter(m);
        }
    }

    /**
     * Convert the Setter method to a {@link BiConsumer} instance.
     * <p>JDK8: for public setters, try LambdaMetafactory, falling back to reflection on failure</p>
     *
     * @param <K> the type of object which holds the method.
     * @param <V> the type of return object.
     * @param m   the required method.
     * @return the BiConsumer instance
     */
    @SuppressWarnings("unchecked")
    public static <K, V> BiConsumer<K, V> createSetter(Method m) {
        try {
            MethodHandle mh = PUBLIC_LOOKUP.unreflect(m);
            mh = mh.asType(MethodType.methodType(void.class, m.getDeclaringClass(), m.getParameterTypes()[0]));
            MethodType invokedType = MethodType.methodType(BiConsumer.class); // () -> BiConsumer
            MethodType samMethodType = MethodType.methodType(void.class, Object.class, Object.class); // BiConsumer.accept(Object,Object)->void
            MethodType instantiatedMethodType = MethodType.methodType(void.class, m.getDeclaringClass(), m.getParameterTypes()[0]); // (T, P)->void
            CallSite cs = LambdaMetafactory.metafactory(
                PUBLIC_LOOKUP,
                "accept",
                invokedType,
                samMethodType,
                mh,
                instantiatedMethodType
            );
            MethodHandle factory = cs.getTarget();
            return (BiConsumer<K, V>) factory.invoke();
        } catch (Throwable t) {
            return reflectionSetter(m);
        }
    }

    /**
     * Reflection fallback getter.
     * <p>Under JPMS on JDK 9+, if the module containing the target class does not {@code opens} the package,
     * {@link Method#setAccessible(boolean)} throws {@code InaccessibleObjectException}.
     * Here, symmetric access handling is performed for getters/setters, and an actionable hint is provided on failure.</p>
     */
    @SuppressWarnings("unchecked")
    private static <K, V> Function<K, V> reflectionGetter(Method m) {
        trySetAccessible(m);
        return (K instance) -> {
            try {
                return (V) m.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }


    /**
     * Reflection fallback setter.
     * <p>Symmetric to {@link #reflectionGetter(Method)}, and likewise handles the JPMS access problem.</p>
     */
    private static <K, V> BiConsumer<K, V> reflectionSetter(Method m) {
        trySetAccessible(m);
        return (K instance, V value) -> {
            try {
                m.invoke(instance, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Attempts to elevate the method's access privileges.
     * <p>In a JPMS environment on JDK 9+, if the module containing the method has not opened its package to the current module,
     * {@code InaccessibleObjectException} is thrown (this class does not exist in JDK 8, so it is checked by class name
     * to keep the bytecode compatible with JDK 8). In that case, if the method itself is public, no privilege elevation
     * is needed and it can continue to be used; otherwise an exception with clear guidance is thrown.</p>
     *
     * @param m the target method
     */
    private static void trySetAccessible(Method m) {
        if (m.isAccessible()) {
            return;
        }
        try {
            m.setAccessible(true);
        } catch (RuntimeException e) {
            // JDK 9+ : InaccessibleObjectException, checked by class name for JDK 8 compilation compatibility
            if ("java.lang.reflect.InaccessibleObjectException".equals(e.getClass().getName())) {
                if (Modifier.isPublic(m.getModifiers()) && Modifier.isPublic(m.getDeclaringClass().getModifiers())) {
                    // A public method needs no privilege elevation; return directly
                    return;
                }
                throw new IllegalStateException(String.format(
                    "Cannot access method '%s.%s'. The declaring class/module does not open its package to this module. " +
                        "Please make the method public, or add 'opens %s;' (or use --add-opens) for the module that declares %s.",
                    m.getDeclaringClass().getName(), m.getName(),
                    m.getDeclaringClass().getPackage() == null ? "" : m.getDeclaringClass().getPackage().getName(),
                    m.getDeclaringClass().getName()), e);
            }
            throw e;
        }
    }
}


