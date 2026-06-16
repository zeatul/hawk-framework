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
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is responsible for
 *
 * @author Hawk
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
     * <p>JDK8: 对public getter尝试用 LambdaMetafactory, 失败回退到反射</p>
     *
     * @param <K> the type of object which holds the method.
     * @param <V> the type of return object.
     * @param m   the required method.
     * @return the Function instance
     */
    @SuppressWarnings("unchecked")
    public static <K, V> Function<K, V> createGetter(Method m) {
        try {
            // 仅对 public 方法且可以用 publicLookup unreflect 的情况有效
            MethodHandle mh = PUBLIC_LOOKUP.unreflect(m);
            // 规范化类型：(T) -> R
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
            // 回退反射
            return reflectionGetter(m);
        }
    }

    /**
     * Convert the Setter method to a {@link BiConsumer} instance.
     * <p>JDK8: 对public setter尝试用 LambdaMetafactory, 失败回退到反射</p>
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
     * 反射回退Getter
     */
    @SuppressWarnings("unchecked")
    private static <K, V> Function<K, V> reflectionGetter(Method m) {
        return (K instance) -> {
            try {
                return (V) m.invoke(instance);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }


    /**
     * 反射回退Setter
     */
    private static <K, V> BiConsumer<K, V> reflectionSetter(Method m) {
        m.setAccessible(true);
        return (K instance, V value) -> {
            try {
                m.invoke(instance, value);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }
}


