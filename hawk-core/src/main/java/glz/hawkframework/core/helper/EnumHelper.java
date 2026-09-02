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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>使用前必须初始化静态变量{@code i18NMessageSource}</p>
 *
 * @author Zhang Peng
 */
public abstract class EnumHelper {

    private static I18NMessageSource MESSAGE_SOURCE;

    private static final Map<String, Object> DISPLAY_NAME_CACHE = new ConcurrentHashMap<>();
    private static final Object NULL_OBJECT = new Object();

    public EnumHelper(I18NMessageSource i18NMessageSource) {
        MESSAGE_SOURCE = argNotNull(i18NMessageSource, "i18NMessageSource");
    }

    /**
     * 获取枚举的显示名称
     *
     * @param enumConstant 枚举常量，不可为空
     * @param locale       语言，不可为空
     * @return 枚举的显示名称
     */
    public static Optional<String> getDisplayName(@Nonnull Enum<?> enumConstant, @Nonnull Locale locale) {
        String key = argNotNull(enumConstant, "enumConstant").getClass().getName() + "." + enumConstant.name();
        return MESSAGE_SOURCE.getMessage(key, argNotNull(locale, "locale"));
    }

    /**
     * 获取枚举的显示名称列表
     *
     * @param enumClass 枚举类，不可为空
     * @param locale    语言，不可为空
     * @return 枚举的显示名称列表
     */
    public static <T extends Enum<T>> List<EnumDisplay<T>> getDisplayNameList(@Nonnull Class<T> enumClass, @Nonnull Locale locale) {
        List<EnumDisplay<T>> list = new ArrayList<>();
        for (T enumConstant : enumClass.getEnumConstants()) {
            String displayName = getDisplayName(enumConstant, locale).orElseThrow(
                () -> new IllegalStateException(String.format("Found no displayName of enumConstant: %s with locale: %s", enumClass.getCanonicalName() + "." + enumConstant.name(), enumConstant.name())));
            list.add(new EnumDisplay<>(enumConstant, displayName));
        }
        return list;
    }

    /**
     * 通过显示名称获取枚举常量
     *
     * @param enumClass   枚举类，不可为空
     * @param displayName 枚举的显示名称，不可为空
     * @param locale      语言，不可为空
     * @return 枚举常量
     */
    public static <T extends Enum<T>> Optional<T> parseByDisplayName(@Nonnull Class<T> enumClass, @Nonnull String displayName, @Nonnull Locale locale) {
        final String keyForMap = argNotNull(enumClass, "enumClass").getName() + "." + argNotBlank(displayName, "displayName") + "." + argNotNull(locale, "locale").toString();

        Object object = DISPLAY_NAME_CACHE.get(keyForMap);
        if (object == NULL_OBJECT) return Optional.empty();
        if (object != null) return Optional.of((T) object);

        T t = null;
        for (T enumConstant : enumClass.getEnumConstants()) {
            String keyForMessage = enumClass.getName() + "." + enumConstant.name();
            String message = MESSAGE_SOURCE.getMessage(keyForMessage, locale).orElse(null);
            if (displayName.equals(message)) t = enumConstant;
        }

        DISPLAY_NAME_CACHE.putIfAbsent(keyForMap, t == null ? NULL_OBJECT : t);

        return Optional.ofNullable(t);
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
