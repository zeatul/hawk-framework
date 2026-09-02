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
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 这个注解用来校验对象的一组属性的值，符合某种NotNull的条件：
 * <p>至少有一个属性的值不是Null</p>
 * <p>至少有一个属性的值是Null</p>
 * <p>只有一个属性的值不是Null</p>
 * <p>只有一个属性的值是null</p>
 * <p>同时全部不为空</p>
 * <p>同时全部为空</p>
 * <p>同时全部为空或同时全部不为空</p>
 * <p>属性通过全路径名指定，例如a.b.c</p>
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
     * 一组属性的全路径集合
     * <p>至少有两个元素</p>
     */
    String[] fields();

    /**
     * 组非空类型
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
         * 至少有一个属性的值不是null
         */
        AT_LEAST_ONE_IS_NO_NULL,
        /**
         * 至少有一个属性的值是null
         */
        AT_LEAST_ONE_IS_NULL,
        /**
         * 只有一个属性的值不是null
         */
        ONLY_ONE_IS_NOT_NULL,
        /**
         * 只有一个属性的值是null
         */
        ONLY_ONE_IS_NULL,
        /**
         * 同时全部不为空
         */
        ALL_ARE_NOT_NULL,
        /**
         * 同时全部为空
         */
        ALL_ARE_NULL,
        /**
         * 同时全部为空或同时全部不为空
         */
        ALL_ARE_NOT_NULL_OR_ALL_ARE_NULL
    }
}
