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

import javax.annotation.Nonnull;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class provides some useful logic operating function.
 *
 * @author Hawk
 */
public abstract class LogicSupport {

    /**
     * If {@code 'object'} is not null, then execute {@code 'consumer'} synchronously, else do nothing.
     *
     * @param object   the parameter to check
     * @param consumer the logic to execute.
     */
    public static <T> void consumeIfNotNull(T object, Consumer<T> consumer) {
        if (object != null) {
            consumer.accept(object);
        }
    }

    public static <T> void consumeIfTrue(Supplier<Boolean> booleanSupplier, Runnable runnable) {
        if (booleanSupplier.get() != null && booleanSupplier.get()) {
            runnable.run();
        }
    }

   public static <T extends Iterable<?>> void consumeIfNotNull(T object,String objectName, Consumer<T> consumer){
        if (object !=null){
            ArgumentSupport.argNoNullElement(object,objectName);
            consumer.accept(object);
        }
   }

    /**
     * If the result of {@code booleanExpression} is {@code false}, then throws the given exception.
     *
     * @param <K>               the type of exception to throw
     * @param booleanExpression the required boolean expression
     * @param exceptionSupplier the supplier to return an exception to throw. (Never {@code null})
     */
    public static <K extends Throwable> void throwIfFalse(boolean booleanExpression, @Nonnull Supplier<? extends K> exceptionSupplier) throws K {
        if (!booleanExpression) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If the result of {@code booleanExpression} is {@code false}, then throws the given exception.
     *
     * @param <K>                       the type of exception to throw
     * @param booleanExpressionSupplier the supplier to return a boolean result.
     * @param exceptionSupplier         the supplier to return an exception to throw.
     */
    public static <K extends Throwable> void throwIfFalse(@Nonnull Supplier<Boolean> booleanExpressionSupplier, @Nonnull Supplier<? extends K> exceptionSupplier) throws K {
        if (!booleanExpressionSupplier.get()) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If the result of {@code booleanExpression} is {@code true}, then throws the given exception.
     *
     * @param <K>               the type of exception to throw
     * @param booleanExpression the required boolean expression
     * @param exceptionSupplier the supplier to return an exception to throw. (Never {@code null})
     */
    public static <K extends Throwable> void throwIfTrue(boolean booleanExpression, @Nonnull Supplier<? extends K> exceptionSupplier) throws K {
        if (booleanExpression) {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If the result of {@code booleanExpression} is {@code true}, then throws the given exception.
     *
     * @param <K>                       the type of exception to throw
     * @param booleanExpressionSupplier the supplier to return a boolean result.
     * @param exceptionSupplier         the supplier to return an exception to throw.
     */
    public static <K extends Throwable> void throwIfTrue(@Nonnull Supplier<Boolean> booleanExpressionSupplier, @Nonnull Supplier<? extends K> exceptionSupplier) throws K {
        if (booleanExpressionSupplier.get()) {
            throw exceptionSupplier.get();
        }
    }
}
