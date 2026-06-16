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
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public abstract class ReflectHelper {

    /**
     * 构造对象实例
     * <p>使用该方法必须保证能够根据参数值精确推导出对应的构造函数的参数类型.</p>
     *
     * @param clazz
     * @param args
     * @param <T>
     * @return
     */

    /**
     * 构造对象实例
     * <p>使用该方法必须保证能够根据参数值精确推导出对应的构造函数的参数类型.</p>
     *
     * @param <T>   要构造的对象的类型
     * @param clazz 要构造的对象的类
     * @param args  传入的构造方法参数
     * @return 对象实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T instance(Class<T> clazz, Object... args) {
        try {
            MethodHandles.Lookup lookup = MethodHandles.lookup();
            if (args == null || args.length == 0) {
                MethodHandle mh = lookup.findConstructor(clazz, MethodType.methodType(void.class));
                return (T) mh.invokeWithArguments();
            } else {
                Class<?> p0 = args[0] == null ? Object.class : args[0].getClass();
                Class<?>[] left = new Class<?>[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    left[i - 1] = args[i] == null ? Object.class : args[i].getClass();
                }
                MethodHandle mh = lookup.findConstructor(clazz, MethodType.methodType(void.class, p0, left));
                return (T) mh.invokeWithArguments(args);
            }
        } catch (java.lang.Throwable e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 构造对象实例
     * <p>传入的参数类型必须和构造函数的参数类型精确匹配</p>
     *
     * @param <T>      要构造的对象的类型
     * @param clazz    要构造的对象的类
     * @param argTypes 构造方法参数类型组
     * @param args     传入的构造方法参数
     * @return 对象实例
     */
    @SuppressWarnings("unchecked")
    public static <T> T instance(Class<T> clazz, Class<?>[] argTypes, Object... args) {
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
     * 根据Field获取对应的Getter
     *
     * @param field the required field.
     * @return a Getter instance.
     */
    public static Method getGetter(Field field) {
        try {
            String getterName = "get" + StringHelper.capitalize(field.getName());
            return field.getDeclaringClass().getDeclaredMethod(getterName);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据Field获取对应的Setter
     *
     * @param field the required Setter.
     * @return a setter instance.
     */
    public static @Nonnull Method getSetter(@Nonnull Field field) {
        try {
            String setterName = "set" + StringHelper.capitalize(field.getName());
            return field.getDeclaringClass().getDeclaredMethod(setterName, field.getType());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
