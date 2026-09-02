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

package glz.hawkframework.dao.column;

import glz.hawkframework.core.helper.LambdaHelper;
import glz.hawkframework.core.helper.ReflectHelper;
import glz.hawkframework.core.helper.StringHelper;
import glz.hawkframework.dao.column.annotation.ColumnFunction;
import org.springframework.core.annotation.MergedAnnotations;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
@SuppressWarnings("unchecked")
public class PojoFieldParser<K> {

    private final Class<K> pojoClass;

    public PojoFieldParser(@Nonnull Class<K> pojoClass) {
        this.pojoClass = argNotNull(pojoClass, "pojoClass");
    }

    @Nonnull
    public <V, Z extends Annotation> Map<Class<? extends Annotation>, List<FieldDescriptor<K, V, Z>>> parse() {
        Map<Class<? extends Annotation>, List<FieldDescriptor<K, V, Z>>> map = new HashMap<>();
        MethodHandles.Lookup publicLookup = MethodHandles.publicLookup();
        Arrays.stream(pojoClass.getDeclaredFields()).forEach(field ->
            Arrays.stream(field.getAnnotations()).forEach(annotation -> {
                MergedAnnotations mergedAnnotations = MergedAnnotations.from(annotation);
                if (!mergedAnnotations.isPresent(ColumnFunction.class)) {
                    return;
                }
                Method updatedGetter = getUpdatedGetter(field);
                FieldDescriptor<K, V, Z> fieldDescriptor = new FieldDescriptor<>(
                    field.getName(),
                    (Z) annotation,
                    LambdaHelper.createGetter(ReflectHelper.getGetter(field)),
                    updatedGetter == null ? null : LambdaHelper.createGetter(updatedGetter),
                    LambdaHelper.createSetter(ReflectHelper.getSetter(field))
//                                        createGetter(getGetter(field), publicLookup),
//                    updatedGetter == null ? null : createGetter(updatedGetter, publicLookup),
//                    createSetter(getSetter(field), publicLookup)
                );
                map.computeIfAbsent(annotation.annotationType(), key -> new ArrayList<>()).add(fieldDescriptor);
            }));
        return map;
    }

//    protected Method getGetter(Field field) {
//        try {
//            return pojoClass.getDeclaredMethod(getGetterName(field.getName()));
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//
//    protected Method getSetter(Field field) {
//        try {
//            return pojoClass.getDeclaredMethod(getSetterName(field.getName()), field.getType());
//        } catch (NoSuchMethodException e) {
//            throw new RuntimeException(e);
//        }
//    }

    protected Method getUpdatedGetter(Field field) {
        try {
            return pojoClass.getDeclaredMethod(getUpdatedGetterName(field.getName()));
        } catch (NoSuchMethodException ignored) {
            return null;
        }
    }

//    protected String getGetterName(String fieldName) {
//        return "get" + StringHelper.capitalize(fieldName);
//    }
//
//    protected String getSetterName(String fieldName) {
//        return "set" + StringHelper.capitalize(fieldName);
//    }

    protected String getUpdatedGetterName(String fieldName) {
        return "is" + StringHelper.capitalize(fieldName) + "Updated";
    }

//    /**
//     * JDK8: 对 public getter 尝试用 LambdaMetafactory, 失败回退到反射
//     */
//    protected <V> Function<K, V> createGetter(Method m, MethodHandles.Lookup publicLookup) {
//        try {
//            // 仅对 public 方法且可以用 publicLookup unreflect 的情况有效
//            MethodHandle mh = publicLookup.unreflect(m);
//            // 规范化类型：(T) -> R
//            mh = mh.asType(MethodType.methodType(m.getReturnType(), pojoClass));
//            MethodType invokedType = MethodType.methodType(Function.class); // () -> Function
//            MethodType samMethodType = MethodType.methodType(m.getReturnType(), pojoClass); // Function.apply(Object)->Object (erased)
//            MethodType instantiatedMethodType = MethodType.methodType(m.getReturnType(), pojoClass); // (T)->R
//            CallSite cs = LambdaMetafactory.metafactory(
//                publicLookup,
//                "apply",
//                invokedType,
//                samMethodType,
//                mh,
//                instantiatedMethodType
//            );
//            MethodHandle factory = cs.getTarget();
//            return (Function<K, V>) factory.invoke();
//        } catch (Throwable t) {
//            // 回退反射
//            return reflectionGetter(m);
//        }
//    }
//
//    // 反射回退 getter
//    protected <V> Function<K, V> reflectionGetter(Method m) {
//        return (K instance) -> {
//            try {
//                return (V) m.invoke(instance);
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        };
//    }
//
//    /**
//     * JDK8: 对 public setter 尝试用 LambdaMetafactory, 失败回退到反射
//     */
//    private <V> BiConsumer<K, V> createSetter(Method m, MethodHandles.Lookup publicLookup) {
//        try {
//            MethodHandle mh = publicLookup.unreflect(m);
//            mh = mh.asType(MethodType.methodType(void.class, pojoClass, m.getParameterTypes()[0]));
//            MethodType invokedType = MethodType.methodType(BiConsumer.class); // () -> BiConsumer
//            MethodType samMethodType = MethodType.methodType(void.class, Object.class, Object.class); // BiConsumer.accept(Object,Object)->void
//            MethodType instantiatedMethodType = MethodType.methodType(void.class, pojoClass, m.getParameterTypes()[0]); // (T, P)->void
//            CallSite cs = LambdaMetafactory.metafactory(
//                publicLookup,
//                "accept",
//                invokedType,
//                samMethodType,
//                mh,
//                instantiatedMethodType
//            );
//            MethodHandle factory = cs.getTarget();
//            return (BiConsumer<K, V>) factory.invoke();
//        } catch (Throwable t) {
//            return reflectionSetter(m);
//        }
//    }
//
//    // 反射回退 setter
//    protected <V> BiConsumer<K, V> reflectionSetter(Method m) {
//        m.setAccessible(true);
//        return (K instance, V value) -> {
//            try {
//                m.invoke(instance, value);
//            } catch (IllegalAccessException | InvocationTargetException e) {
//                throw new RuntimeException(e);
//            }
//        };
//    }
}
