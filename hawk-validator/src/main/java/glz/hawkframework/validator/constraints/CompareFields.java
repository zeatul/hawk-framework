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

import glz.hawkframework.validator.validators.CompareFieldsValidator.CompareFieldsValidatorForObject;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 这个注解用来校验一个对象的两个属性的比较结果符合预期
 *
 * @author Zhang Peng
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(CompareFields.List.class)
@Documented
@Constraint(validatedBy = CompareFieldsValidatorForObject.class)
public @interface CompareFields {

    String DEFAULT_MESSAGE_TEMPLATE = "{glz.hawkframework.validator.constraints.CompareFields.%s.message}";

    String message() default DEFAULT_MESSAGE_TEMPLATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * 比较的左值的属性全路径
     */
    String left();

    /**
     * 比较的右值的属性全路径
     */
    String right();

    /**
     * 预期结果
     */
    CompareResult expectedResult();

    /**
     * Defines several {@link CompareFields} annotations on the same element.
     *
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        CompareFields[] value();
    }


    enum CompareResult {
        /**
         * left less than right
         */
        LESS,
        /**
         * left greater than right
         */
        GREATER,
        /**
         * left equal to right
         */
        EQUAL,
        /**
         * left less or equal to right
         */
        LESS_OR_EQUAL,
        /**
         * left greater or equal to right
         */
        GREATER_OR_EQUAL,
        /**
         * left not equal to right
         */
        NOT_EQUAL
    }


}
