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
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This class is a mimic of {@link Optional} and provides some useful logic methods to operate the {@code value} of it.
 *
 * @author Hawk
 */
public class LogicOptional<T> {
    private static final LogicOptional<?> EMPTY = new LogicOptional<>();
    private final T value;

    private LogicOptional(T value) {
        this.value = value;
    }

    private LogicOptional() {
        this.value = null;
    }

    @Nonnull
    public static <T> LogicOptional<T> of(@Nullable T value) {
        return new LogicOptional<>(value);
    }

    public static <T> LogicOptional<T> empty() {
        @SuppressWarnings("unchecked")
        LogicOptional<T> t = (LogicOptional<T>) EMPTY;
        return t;
    }

    public LogicOptional<T> ifPresent(Consumer<? super T> consumer) {
        if (value != null) {
            consumer.accept(value);
        }
        return this;
    }

    public <X extends Throwable> LogicOptional<T> notNull(Supplier<? extends X> exceptionSupplier) throws X {
        if (value != null) {
            return this;
        } else {
            throw exceptionSupplier.get();
        }
    }

    public LogicOptional<T> argNotNull(@Nonnull String argName) {
        ArgumentSupport.argNotNull(this.value, argName);
        return this;
    }

    public <X extends Throwable> LogicOptional<T> isTrueOrThrow(Function<T, Boolean> booleanFunction, Supplier<? extends X> exceptionSupplier) throws X {
        if (booleanFunction.apply(value)) {
            return this;
        } else {
            throw exceptionSupplier.get();
        }
    }

    /**
     * If else do
     *
     * @param booleanFunction the function to check the value of current LogicOptional instance
     * @param workForTrue     the work for {@code true}
     * @param workForFalse    the work for {@code false}
     * @return current LogicOptional instance.
     */
    public LogicOptional<T> ifElse(Function<T, Boolean> booleanFunction, Consumer<T> workForTrue, Consumer<T> workForFalse) {
        if (booleanFunction.apply(value)) {
            workForTrue.accept(value);
        } else {
            workForFalse.accept(value);
        }
        return this;
    }

    public <U> LogicOptional<U> map(@Nonnull Function<? super T, ? extends U> mapper) {
        ArgumentSupport.argNotNull(mapper, "mapper");
        if (this.value == null)
            return empty();
        else {
            return LogicOptional.of(mapper.apply(value));
        }
    }

    public T get() {
        return this.value;
    }


}
