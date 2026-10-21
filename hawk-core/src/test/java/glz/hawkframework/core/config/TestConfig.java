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

package glz.hawkframework.core.config;

import glz.hawkframework.core.helper.EnumHelper;
import glz.hawkframework.core.i18n.I18NMessageSource;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.util.Locale;
import java.util.Optional;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
@AutoConfiguration
public class TestConfig {

//    @Bean
//    public MessageSource messageSource() {
//        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
//        messageSource.setDefaultEncoding("UTF-8");
//        messageSource.setBasenames("classpath:glz/hawk/bookstore/pub/enums/enum_display");
//        return messageSource;
//    }

    @Bean
    public void initEnumHelper() {

        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasenames("classpath:glz/hawk/bookstore/pub/enums/enum_display");

        I18NMessageSource i18NMessageSource = new I18NMessageSource() {
            private final static String DEFAULT_MESSAGE = "$$$NO_MESSAGE_FOUND$$$";
            @Override
            public Optional<String> getMessage(@NonNull String key, @NonNull Locale locale) {
                String message =  messageSource.getMessage(key,null,DEFAULT_MESSAGE,locale);
                return DEFAULT_MESSAGE.equals(message) ? Optional.empty() : Optional.ofNullable( message);
            }
        };

        EnumHelper.initialize(i18NMessageSource);

    }
}
