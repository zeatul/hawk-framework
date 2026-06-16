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
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 这个注解自己定义校验的方法，校验的消息模板，因此可以校验任何复杂情况
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
     * 校验方法的名称
     * <p>{@link UniversalCheckValidator}通过该名称来定位校验方法和校验消息模板</p>
     * <p>定位校验方法是，用来作为{@link UniversalCheckSupport#loadChecker}方法的输入参数</p>
     * <p>定位校验方法是，用来作为{@link UniversalCheckSupport#findMessageParameterSupplier}方法的输入参数</p>
     * <p>定位消息模板时，用来替换{@link #DEFAULT_MESSAGE_TEMPLATE}里的{@code %s}</p>
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
