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


import glz.hawkframework.validator.validators.EnumValueValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation is responsible for
 * <p>Validating whether the given value is one of the enum's allowed values.</p>
 *
 * @author Zhang Peng
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(EnumValue.List.class)
@Documented
@Constraint(validatedBy = EnumValueValidator.EnumValueValidatorForObject.class)
public @interface EnumValue {

    String message() default "{glz.hawkframework.validator.constraints.EnumValue.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * This attribute defines the class of the enum for check.
     * <p>This attribute is prior to {@link #enumClassName()}.</p>
     */
    Class<? extends Enum> enumClass() default Enum.class;

    /**
     * This attribute defines the fully qualified class name of the enum.
     */
    String enumClassName() default "";

    /**
     * Gets an array of enum instances' name.
     * <p>If the value of this attribute is empty, the validated value can match any enum instance's value.</p>
     * <P>If the value of this attribute is not empty, the validated value only can match the provided enum instances' value.</p>
     *
     * @see Enum#name()
     */
    String[] includeNames() default {};

    /**
     * Gets an array of enum instances' name.
     *
     * @see Enum#name()
     */
    String[] excludeNames() default {};


    /**
     * Defines several {@link EnumValue} annotations on the same element.
     *
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        EnumValue[] value();
    }

}
