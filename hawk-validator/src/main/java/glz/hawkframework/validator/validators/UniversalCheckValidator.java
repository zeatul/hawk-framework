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

package glz.hawkframework.validator.validators;

import glz.hawkframework.validator.constraints.UniversalCheck;
import glz.hawkframework.validator.support.UniversalCheckSupport;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.function.Function;

import static glz.hawkframework.validator.constraints.UniversalCheck.DEFAULT_MESSAGE_TEMPLATE;

/**
 * This class is responsible for
 * <p>使用自定义方法校验</p>
 *
 * @author Zhang Peng
 */
public class UniversalCheckValidator implements ConstraintValidator<UniversalCheck, Object> {

    private UniversalCheck universalCheck;

    @Override
    public void initialize(UniversalCheck constraintAnnotation) {
        this.universalCheck = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) return true;
        Function<Object, Boolean> function = UniversalCheckSupport.loadChecker(universalCheck.checkerName());
        boolean isValid = function.apply(value);
        if (!isValid) {
            if (universalCheck.message().equals(DEFAULT_MESSAGE_TEMPLATE)) {
                context.disableDefaultConstraintViolation();
                HibernateConstraintValidatorContext hibernateContext = context.unwrap(HibernateConstraintValidatorContext.class);
                Function<Object, Map<String, Object>> messageParameterSupplier = UniversalCheckSupport.findMessageParameterSupplier(universalCheck.checkerName());
                if (messageParameterSupplier != null) {
                    Map<String, Object> messageParameters = messageParameterSupplier.apply(value);
                    if (messageParameters != null) {
                        for (Map.Entry<String, Object> entry : messageParameters.entrySet()) {
                            hibernateContext.addMessageParameter(entry.getKey(), entry.getValue());
                        }
                    }
                }
                hibernateContext.buildConstraintViolationWithTemplate(String.format(universalCheck.message(), universalCheck.checkerName())).addConstraintViolation();
            }
        }
        return isValid;
    }
}
