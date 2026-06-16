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

import glz.hawkframework.core.helper.StringHelper;
import glz.hawkframework.validator.constraints.EnumName;
import glz.hawkframework.validator.support.EnumSupport;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class EnumNameValidator<T> implements ConstraintValidator<EnumName, T> {

    private EnumName enumCheck;

    /**
     * This method is called before the {@link #isValid} method
     */
    @Override
    public void initialize(EnumName constraintAnnotation) {
        this.enumCheck = constraintAnnotation;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) return true;
        Class<? extends Enum> enumClass = enumCheck.enumClass();
        if (enumClass == Enum.class) {
            String enumClassName = enumCheck.enumClassName();
            if (StringHelper.isBlank(enumClassName)) {
                throw new IllegalStateException("Please provide a valid enumClass or a valid enumClassName");
            }
            try {
                enumClass = (Class<? extends Enum>) Class.forName(enumClassName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        Enum<?> parsed = EnumSupport.fromName(enumClass, value.toString());
        if (parsed == null) return false;

        String[] excludeNames = enumCheck.excludeNames();
        for (String excludeName : excludeNames) {
            if (parsed.name().equals(excludeName)) return false;
        }

        String[] includeNames = enumCheck.includeNames();
        if (includeNames.length == 0) return true;
        for (String includeName : includeNames) {
            if (parsed.name().equals(includeName)) return true;
        }

        return false;
    }

    public static class EnumValueValidatorForCharSequence extends EnumNameValidator<CharSequence> {
    }
}
