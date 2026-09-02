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

package glz.hawkframework.validator.constraints;

import glz.hawkframework.validator.constraints.config.TestConfig;
import glz.hawkframework.validator.constraints.support.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Locale;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
@SpringBootTest(classes = TestConfig.class)
public class I18NTest {

    @Autowired
    private Validator validator;

    @Test
    public void is_chinese() {
        LocaleContextHolder.setLocale(Locale.CHINA);
        Person person = new Person();
        Set<ConstraintViolation<Person>> set = validator.validate(person);
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next().getMessage()).isEqualTo("不能为空");
    }

    @Test
    public void is_english() {
        LocaleContextHolder.setLocale(Locale.ENGLISH);
        Person person = new Person();
        Set<ConstraintViolation<Person>> set = validator.validate(person);
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next().getMessage()).isEqualTo("must not be blank");
    }
}
