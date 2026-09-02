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

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Locale;
import java.util.Optional;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This class is responsible for
 * <p></p>
 * <p>需要初始化{@link I18NMessageSourceHolder}</p>
 *
 * @author Zhang Peng
 */
public class I18NDisplay implements Display {

    private final String key;

    public I18NDisplay(String key) {
        this.key = argNotBlank(key, "key");
    }

    @Override
    public Optional<String> getDisplay(@NonNull Locale locale) {
        return I18NMessageSourceHolder.getMessage(key, locale);
    }
}
