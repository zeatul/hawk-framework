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
import glz.hawkframework.validator.constraints.support.ObjectForLess;
import glz.hawkframework.validator.constraints.support.ObjectForUniversalCheck;
import glz.hawkframework.validator.support.UniversalCheckSupport;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
@SpringBootTest(classes = TestConfig.class)
public class UniversalCheckTest {
    @Autowired
    private Validator validator;

    @BeforeAll
    public static void setup() {
        LocaleContextHolder.setDefaultLocale(Locale.CHINA);
        final String checkerName = "LESS";
        UniversalCheckSupport.addChecker(checkerName, (ObjectForUniversalCheck o) -> {
            if (o.getAge1() == null || o.getAge2() == null) return true;
            return (o.getAge1().compareTo(o.getAge2()) < 0);
        });
        UniversalCheckSupport.addMessageParameterSupplier(checkerName, (ObjectForUniversalCheck o) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("left", "age1");
            map.put("right", "age2");
            return map;
        });
    }

    @Test
    public void test_has_one_error() {
        ObjectForUniversalCheck o = new ObjectForUniversalCheck();
        o.setAge1(100);
        o.setAge2(50);
        Set<ConstraintViolation<ObjectForUniversalCheck>> set = validator.validate(o);
        assertThat(set).hasSize(1);
        assertThat(set.iterator().next().getMessage()).isEqualTo("age1的值必须小于age2的值");
    }

    @Test
    public void test_has_no_error() {
        ObjectForUniversalCheck o = new ObjectForUniversalCheck();
        o.setAge1(100);
        o.setAge2(501);
        Set<ConstraintViolation<ObjectForUniversalCheck>> set = validator.validate(o);
        assertThat(set).hasSize(0);
    }
}
