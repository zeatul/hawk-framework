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
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public abstract class LocaleHelper {

    private final static Map<String, Locale> map = new ConcurrentHashMap<>();

    /**
     * 分析locale，取出其中的语言段
     *
     * @param locale
     * @return
     */
    public static String getLanguage(String locale) {
        argNotBlank(locale, "locale");
        if (locale.length() == 2) {
            return locale;
        } else if (locale.length() == 5) {
            return locale.substring(0, 2);
        } else {
            throw new IllegalArgumentException("The length of local must be 2 or 5.");
        }
    }

    /**
     * 将{@code localeStr}翻译成标准的{@code -}分割的格式
     *
     * @param localeStr the locale string to format
     * @return
     */
    public static String format(String localeStr) {
        argNotBlank(localeStr, "localeStr");
        if (localeStr.length() == 2) {
            return localeStr;
        } else if (localeStr.length() == 5) {
            return localeStr.substring(0, 2) + "-" + localeStr.substring(3, 5);
        } else {
            throw new IllegalArgumentException("The length of locale must be 2 or 5.");
        }
    }

    /**
     * 解析失败，抛异常
     *
     * @param localeStr 不可为空
     * @return a Locale instance
     */
    public static Locale parse(@Nonnull String localeStr) {
        localeStr = format(localeStr);
        Locale locale = map.computeIfAbsent(localeStr, Locale::forLanguageTag);
        if (locale == null) throw new IllegalStateException("Failed to parse locale: " + localeStr);
        return locale;
    }

    /**
     * 获取不包含国家代码的Locale
     *
     * @param locale
     * @return
     */
    public static @Nonnull Locale getLocaleWithoutCountry(@Nonnull Locale locale) {
        return Locale.forLanguageTag(locale.getLanguage());
    }
}
