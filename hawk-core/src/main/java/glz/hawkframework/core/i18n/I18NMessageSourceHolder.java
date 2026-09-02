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

import javax.annotation.Nonnull;
import java.util.Locale;
import java.util.Optional;

/**
 * This class is responsible for
 * <p></p>
 * * @author Zhang Peng
 */
public class I18NMessageSourceHolder {
    private static I18NMessageSource i18NMessageSource;

    public I18NMessageSourceHolder(I18NMessageSource i18NMessageSource) {
        I18NMessageSourceHolder.i18NMessageSource = i18NMessageSource;
    }

    public static Optional<String> getMessage(@Nonnull String key, @Nonnull Locale locale) {
        return i18NMessageSource.getMessage(key, locale);
    }


}
