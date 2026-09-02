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

package glz.hawkframework.core.enums;

import glz.hawkframework.core.helper.MapHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.StateSupport.stateNotNull;

/**
 * {@link NumberBoolean} defines {@code 1} as true and {@code 0} as false.
 *
 * @author Hawk
 */
public enum NumberBoolean {

    /**
     * {@code true}
     */
    TRUE(1),

    /**
     * {@code false}
     */
    FALSE(0);

    public final int value;

    private NumberBoolean(int value) {
        this.value = value;
    }

    private final static Map<Integer, NumberBoolean> valueMap =
        MapHelper.<Integer, NumberBoolean>builder()
            .put(TRUE.value, TRUE)
            .put(FALSE.value, FALSE)
            .buildHashMap();

    private final static Map<String, NumberBoolean> nameMap =
        MapHelper.<String, NumberBoolean>builder()
            .put(TRUE.name(), TRUE)
            .put(FALSE.name(), FALSE)
            .buildHashMap();

    public static @Nullable NumberBoolean fromValue(@Nullable Integer value) {
        if (value == null) return null;
        return valueMap.get(value);
    }

    public static @Nullable NumberBoolean fromName(@Nullable String name) {
        if (name == null) return null;
        return nameMap.get(name);
    }

    public static @Nonnull NumberBoolean parseByValue(@Nonnull Integer value) {
        return stateNotNull(fromValue(argNotNull(value)), () -> String.format("Found no NumberBoolean with value %d", value));
    }

    public static @Nonnull NumberBoolean parseByName(@Nonnull String name) {
        return stateNotNull(fromName(argNotNull(name)), () -> String.format("Found no NumberBoolean named %s", name));
    }

}
