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

import glz.hawkframework.validator.validators.CompareFieldsValidator;
import glz.hawkframework.validator.validators.NotNullGroupValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is used to validate the values of a group of fields of an object against some NotNull condition:
 * <p>At least one field's value is not null.</p>
 * <p>At least one field's value is null.</p>
 * <p>Only one field's value is not null.</p>
 * <p>Only one field's value is null.</p>
 * <p>All are not null at the same time.</p>
 * <p>All are null at the same time.</p>
 * <p>All are null at the same time or all are not null at the same time.</p>
 * <p>Fields are specified by their full path names, for example a.b.c.</p>
 *
 * @author Zhang Peng
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(NotNullGroup.List.class)
@Documented
@Constraint(validatedBy = NotNullGroupValidator.class)
public @interface NotNullGroup {

    String DEFAULT_MESSAGE_TEMPLATE = "{glz.hawkframework.validator.constraints.NotNullGroup.%s.message}";

    String message() default DEFAULT_MESSAGE_TEMPLATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The set of full paths of a group of fields.
     * <p>Must contain at least two elements.</p>
     */
    String[] fields();

    /**
     * The not-null type of the group.
     */
    NotNullType type();

    /**
     * Defines several {@link NotNullGroup} annotations on the same element.
     *
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        NotNullGroup[] value();
    }


    enum NotNullType {
        /**
         * At least one field's value is not null.
         */
        AT_LEAST_ONE_IS_NOT_NULL,
        /**
         * At least one field's value is null.
         */
        AT_LEAST_ONE_IS_NULL,
        /**
         * Only one field's value is not null.
         */
        ONLY_ONE_IS_NOT_NULL,
        /**
         * Only one field's value is null.
         */
        ONLY_ONE_IS_NULL,
        /**
         * All are not null at the same time.
         */
        ALL_ARE_NOT_NULL,
        /**
         * All are null at the same time.
         */
        ALL_ARE_NULL,
        /**
         * All are null at the same time or all are not null at the same time.
         */
        ALL_ARE_NOT_NULL_OR_ALL_ARE_NULL
    }
}
