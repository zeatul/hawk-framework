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
import glz.hawkframework.validator.constraints.support.Book;
import org.junit.jupiter.api.BeforeEach;
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
 * @author Zhang Peng
 */
@SpringBootTest(classes = TestConfig.class)
public class EnumNameTest {

    @Autowired
    private Validator validator;

    @BeforeEach
    public void setup(){
        LocaleContextHolder.setDefaultLocale(Locale.CHINA);
    }


    @Test
    public void has_one_error() {
        Book book = new Book();
        book.setBookCategoryName("100");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next().getMessage()).isEqualTo("不是有效的枚举名称");
    }

    @Test
    public void has_no_error() {
        Book book = new Book();
        book.setBookCategoryName("ENGLISH");
        Set<ConstraintViolation<Book>> set = validator.validate(book);
        assertThat(set).hasSize(0);
    }


}
