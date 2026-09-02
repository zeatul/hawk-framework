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

package glz.hawkframework.core.i18n;

import glz.hawkframework.core.helper.LocaleHelper;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class SimpleDisplay implements Display {
    private final static String EMPTY_LOCALE = "NN-NN";
    private final Map<String, String> localeDisplayMap = new HashMap<>();

    private SimpleDisplay() {
    }

    public SimpleDisplay(Builder builder) {
        this.localeDisplayMap.putAll(builder.localeDisplayMap);
    }

    public Optional<String> getDisplay(@Nonnull String locale) {
        locale = LocaleHelper.format(locale);
        String display = localeDisplayMap.get(argNotBlank(locale, "locale"));

        if (display == null && locale.length() == 5) {
            display = localeDisplayMap.get(LocaleHelper.getLanguage(locale));
        }

        if (display == null) {
            display = localeDisplayMap.get(EMPTY_LOCALE);
        }

        return Optional.ofNullable(display);
    }

    public Optional<String> getDisplay(@Nonnull Locale locale) {
        return getDisplay(argNotNull(locale, "locale").toString());
    }

    public static class Builder {
        private final Map<String, String> localeDisplayMap = new HashMap<>();

        public Builder add(String locale, String display) {
            locale = LocaleHelper.format(locale);
            argNotBlank(display, "display");
            if (localeDisplayMap.putIfAbsent(locale, display) != null) throw new IllegalStateException("duplicate locale: " + locale);
            return this;
        }

        public Builder add(String display) {
            return add(EMPTY_LOCALE, display);
        }
    }
}
