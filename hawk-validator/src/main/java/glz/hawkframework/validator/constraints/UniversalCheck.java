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

import glz.hawkframework.validator.support.UniversalCheckSupport;
import glz.hawkframework.validator.validators.UniversalCheckValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * This annotation defines its own validation method and validation message template, so it can validate any complex case.
 *
 * @author Zhang Peng
 */
@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(UniversalCheck.List.class)
@Documented
@Constraint(validatedBy = UniversalCheckValidator.class)
public @interface UniversalCheck {

    String DEFAULT_MESSAGE_TEMPLATE = "{glz.hawkframework.validator.constraints.UniversalCheck.%s.message}";

    String message() default DEFAULT_MESSAGE_TEMPLATE;

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    /**
     * The name of the validation method.
     * <p>{@link UniversalCheckValidator} uses this name to locate the validation method and the validation message template.</p>
     * <p>When locating the validation method, it is used as the input parameter of the {@link UniversalCheckSupport#loadChecker} method.</p>
     * <p>When locating the validation method, it is used as the input parameter of the {@link UniversalCheckSupport#findMessageParameterSupplier} method.</p>
     * <p>When locating the message template, it is used to replace the {@code %s} in {@link #DEFAULT_MESSAGE_TEMPLATE}.</p>
     * <p><b>Naming convention: a fully-qualified name must be used</b> (for example {@code "com.example.order.OrderAgeCheck"}),
     * to avoid conflicts when validators with the same name are registered by different modules/applications.</p>
     */
    String checkerName();

    /**
     * Defines several {@link UniversalCheck} annotations on the same element.
     *
     */
    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        UniversalCheck[] value();
    }
}
