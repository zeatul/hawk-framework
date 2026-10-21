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
import java.util.regex.Pattern;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public abstract class LocaleHelper {

    private final static Map<String, Locale> map = new ConcurrentHashMap<>();

    private static final Pattern LANGUAGE_TAG_PATTERN = Pattern.compile(
            "^([a-z]{2,3}(-[a-z]{4})?(-([a-z]{2}|[0-9]{3}))?(-[a-z0-9]{5,8})*|[a-z]{5,8})$",
            Pattern.CASE_INSENSITIVE);

    public static String getLanguage(String locale) {
        argNotBlank(locale, "locale");
        String normalized = locale.replace('_', '-');
        if (!isValidLanguageTag(normalized)) {
            if (locale.length() == 2) return locale;
            if (locale.length() == 5) return locale.substring(0, 2);
            throw new IllegalArgumentException("Invalid locale format: " + locale);
        }
        Locale l = Locale.forLanguageTag(normalized);
        String lang = l.getLanguage();
        if (!lang.isEmpty()) return lang;
        if (locale.length() == 2) return locale;
        if (locale.length() == 5) return locale.substring(0, 2);
        throw new IllegalArgumentException("Invalid locale format: " + locale);
    }

    public static String format(String localeStr) {
        argNotBlank(localeStr, "localeStr");
        String normalized = localeStr.replace('_', '-');
        if (!isValidLanguageTag(normalized)) {
            if (localeStr.length() == 2) return localeStr;
            if (localeStr.length() == 5) return localeStr.substring(0, 2) + "-" + localeStr.substring(3, 5);
            throw new IllegalArgumentException("Invalid locale format: " + localeStr);
        }
        Locale l = Locale.forLanguageTag(normalized);
        if (!l.getLanguage().isEmpty()) return l.toLanguageTag();
        if (localeStr.length() == 2) return localeStr;
        if (localeStr.length() == 5) return localeStr.substring(0, 2) + "-" + localeStr.substring(3, 5);
        throw new IllegalArgumentException("Invalid locale format: " + localeStr);
    }

    private static boolean isValidLanguageTag(String tag) {
        if (!LANGUAGE_TAG_PATTERN.matcher(tag).matches()) return false;
        Locale l = Locale.forLanguageTag(tag);
        String country = l.getCountry();
        if (!country.isEmpty()) {
            return country.length() == 2 && country.matches("[A-Za-z]{2}") || country.length() == 3 && country.matches("[0-9]{3}");
        }
        return true;
    }

    public static Locale parse(@Nonnull String localeStr) {
        localeStr = format(localeStr);
        Locale locale = map.computeIfAbsent(localeStr, Locale::forLanguageTag);
        if (locale == null) throw new IllegalStateException("Failed to parse locale: " + localeStr);
        return locale;
    }

    public static @Nonnull Locale getLocaleWithoutCountry(@Nonnull Locale locale) {
        return Locale.forLanguageTag(locale.getLanguage());
    }
}