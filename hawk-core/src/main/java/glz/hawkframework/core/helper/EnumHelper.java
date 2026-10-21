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

import glz.hawkframework.core.i18n.I18NMessageSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>Must be initialized via {@link #initialize(I18NMessageSource)} before use</p>
 *
 * @author Zhang Peng
 */
public abstract class EnumHelper {

    private static final AtomicReference<I18NMessageSource> MESSAGE_SOURCE_REF = new AtomicReference<>();

    private static final Map<String, Object> DISPLAY_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Object NULL_OBJECT = new Object();

    /**
     * Initializes EnumHelper; must be called once before using any other static method.
     * <p>Thread-safe: only the first call takes effect; subsequent calls are ignored.</p>
     *
     * @param i18NMessageSource the internationalization message source, must not be null
     * @throws IllegalStateException if already initialized and a different instance is passed
     */
    public static void initialize(@Nonnull I18NMessageSource i18NMessageSource) {
        argNotNull(i18NMessageSource, "i18NMessageSource");
        I18NMessageSource existing = MESSAGE_SOURCE_REF.get();
        if (existing != null && existing != i18NMessageSource) {
            throw new IllegalStateException("EnumHelper already initialized with a different I18NMessageSource instance.");
        }
        MESSAGE_SOURCE_REF.compareAndSet(null, i18NMessageSource);
    }

    /**
     * Gets the initialized message source, or returns null if not initialized.
     */
    @Nullable
    private static I18NMessageSource getMessageSource() {
        return MESSAGE_SOURCE_REF.get();
    }

    /**
     * Gets the display name of an enum constant.
     *
     * @param enumConstant the enum constant, must not be null
     * @param locale       the locale, must not be null
     * @return the display name of the enum constant
     * @throws IllegalStateException if not initialized
     */
    public static Optional<String> getDisplayName(@Nonnull Enum<?> enumConstant, @Nonnull Locale locale) {
        I18NMessageSource messageSource = getMessageSource();
        if (messageSource == null) throw new IllegalStateException("EnumHelper is not initialized. Please call EnumHelper.initialize(I18NMessageSource) first.");
        String key = argNotNull(enumConstant, "enumConstant").getClass().getName() + "." + enumConstant.name();
        return messageSource.getMessage(key, argNotNull(locale, "locale"));
    }

    /**
     * Gets the list of display names for an enum.
     *
     * @param enumClass the enum class, must not be null
     * @param locale    the locale, must not be null
     * @return the list of display names for the enum
     * @throws IllegalStateException if not initialized
     */
    public static <T extends Enum<T>> List<EnumDisplay<T>> getDisplayNameList(@Nonnull Class<T> enumClass, @Nonnull Locale locale) {
        I18NMessageSource messageSource = getMessageSource();
        if (messageSource == null) throw new IllegalStateException("EnumHelper is not initialized. Please call EnumHelper.initialize(I18NMessageSource) first.");
        List<EnumDisplay<T>> list = new ArrayList<>();
        for (T enumConstant : enumClass.getEnumConstants()) {
            String displayName = getDisplayName(enumConstant, locale).orElseThrow(
                () -> new IllegalStateException(String.format("Found no displayName of enumConstant: %s with locale: %s", enumClass.getCanonicalName() + "." + enumConstant.name(), enumConstant.name())));
            list.add(new EnumDisplay<>(enumConstant, displayName));
        }
        return list;
    }

    /**
     * Gets an enum constant by its display name.
     *
     * @param enumClass   the enum class, must not be null
     * @param displayName the display name of the enum constant, must not be null
     * @param locale      the locale, must not be null
     * @return the enum constant
     * @throws IllegalStateException if not initialized
     */
    public static <T extends Enum<T>> Optional<T> parseByDisplayName(@Nonnull Class<T> enumClass, @Nonnull String displayName, @Nonnull Locale locale) {
        I18NMessageSource messageSource = getMessageSource();
        if (messageSource == null) throw new IllegalStateException("EnumHelper is not initialized. Please call EnumHelper.initialize(I18NMessageSource) first.");
        final String keyForMap = argNotNull(enumClass, "enumClass").getName() + "." + argNotBlank(displayName, "displayName") + "." + argNotNull(locale, "locale").toString();

        Object object = DISPLAY_NAME_CACHE.get(keyForMap);
        if (object == NULL_OBJECT) return Optional.empty();
        if (object != null) return Optional.of((T) object);

        T t = null;
        for (T enumConstant : enumClass.getEnumConstants()) {
            String keyForMessage = enumClass.getName() + "." + enumConstant.name();
            String message = messageSource.getMessage(keyForMessage, locale).orElse(null);
            if (displayName.equals(message)) {
                t = enumConstant;
                break;
            }
        }

        DISPLAY_NAME_CACHE.putIfAbsent(keyForMap, t == null ? NULL_OBJECT : t);

        return Optional.ofNullable(t);
    }

    /**
     * Clears the display name cache, for testing or hot-reload scenarios.
     */
    public static void clearCache() {
        DISPLAY_NAME_CACHE.clear();
    }

    public static class EnumDisplay<T extends Enum<T>> {
        public final T enumConstant;
        public final String displayName;

        public EnumDisplay(T enumConstant, String displayName) {
            this.enumConstant = enumConstant;
            this.displayName = displayName;
        }
    }

}
