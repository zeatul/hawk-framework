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

package glz.hawkframework.core.enums.inf;

import java.util.Locale;

/**
 * This interface should be inherited by the {@link Enum} class.
 * And every instance of this {@link Enum} class must have a unique value.
 *
 * @param <K> {@code K}, the type of Enum instance's value
 * @param <V> {@code V}, the type of Enum which implements this interface
 * @author Hawk
 */
public interface IValuableEnum<K, V extends Enum<V>> {

    /**
     * Get the value of the {@link Enum} instance.
     *
     * @return the value of this {@link Enum} instance
     */
    K getValue();

    /**
     * Get the name of the {@link Enum} instance's value according to the default {@link Locale}
     *
     * @return the name of the {@link Enum} object's value according to the default {@link Locale}
     */

    default String getNameOfValue() {
        return getNameOfValue(null);
    }

    /**
     * Get the name of the {@link Enum} instance's value according to the specified {@link Locale}
     *
     * @param locale the required {@link Locale}. if it is null, use the default system {@code Locale}.
     * @return localized name of {@link Enum} instance's value; or return {@link Enum#name()} if found none.
     */
    @SuppressWarnings("unchecked")
    default String getNameOfValue(Locale locale) {
        return ((Enum<V>) this).name();
    }

}
