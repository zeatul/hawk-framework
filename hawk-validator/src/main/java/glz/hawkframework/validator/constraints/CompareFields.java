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
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used to validate that the comparison result of two fields of an object matches the expectation.
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
     * The full property path of the left value being compared.
     */
    String left();

    /**
     * The full property path of the right value being compared.
     */
    String right();

    /**
     * The expected result.
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
         * <p>If either value is {@code null}, validation passes (no error is reported).</p>
         */
        LESS,
        /**
         * left greater than right
         * <p>If either value is {@code null}, validation passes (no error is reported).</p>
         */
        GREATER,
        /**
         * left equal to right
         * <p>If either value is {@code null}, validation passes (no error is reported).</p>
         */
        EQUAL,
        /**
         * left less or equal to right
         * <p>If either value is {@code null}, validation passes (no error is reported).</p>
         */
        LESS_OR_EQUAL,
        /**
         * left greater or equal to right
         * <p>If either value is {@code null}, validation passes (no error is reported).</p>
         */
        GREATER_OR_EQUAL,
        /**
         * left not equal to right
         * <p>If either value is {@code null}, validation passes (no error is reported).</p>
         */
        NOT_EQUAL
    }


}
