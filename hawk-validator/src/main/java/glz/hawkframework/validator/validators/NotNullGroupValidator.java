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
import glz.hawkframework.validator.constraints.NotNullGroup;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.validation.ConstraintDeclarationException;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static glz.hawkframework.validator.constraints.NotNullGroup.DEFAULT_MESSAGE_TEMPLATE;

/**
 * This class is used to validate that multiple fields of an object satisfy a certain not-null condition.
 *
 * @author Zhang Peng
 */
public class NotNullGroupValidator implements ConstraintValidator<NotNullGroup, Object> {

    private final ExpressionParser expressionParser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED,   // compiler mode
        null  // class loader, may be null
    ));

    // Use a simple LRU cache, to prevent malicious/dynamic field paths from causing OOM
    // At most 1024 expressions are cached; when exceeded, they are cleared
    private final Map<String, Expression> expressionCache = new LruExpressionCache(1024);

    private NotNullGroup notNullGroup;

    @Override
    public void initialize(NotNullGroup constraintAnnotation) {
        this.notNullGroup = constraintAnnotation;
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        if (value == null) return true;
        String[] fieldPaths = notNullGroup.fields();
        if (fieldPaths.length < 2) throw new IllegalStateException("The fields property of NotNullGroup must have at least two elements.");
        Object[] objects = new Object[fieldPaths.length];
        for (int i = 0; i < fieldPaths.length; i++) {
            String fieldPath = fieldPaths[i];
            if (StringHelper.isBlank(fieldPath)) throw new IllegalStateException("Found blank element in the fields property of NoNullGroup.");
            Expression expression = expressionCache.computeIfAbsent(fieldPath, expressionParser::parseExpression);
            try {
                objects[i] = expression.getValue(value);
            } catch (Exception e) {
                throw new ConstraintDeclarationException("SpEL expression '" + fieldPath + "' evaluation failed", e);
            }
        }

        boolean isValid = verify(objects);

        if (!isValid) {
            if (notNullGroup.message().equals(DEFAULT_MESSAGE_TEMPLATE)) {
                context.disableDefaultConstraintViolation();

                // Convert to the Hibernate-specific Context
                HibernateConstraintValidatorContext hibernateContext = context.unwrap(
                    HibernateConstraintValidatorContext.class
                );

                // Add dynamic variables for use by the message template
//                hibernateContext.addExpressionVariable("fieldNames", String.join(", ", notNullGroup.fields())); // only applies to EL
                hibernateContext.addMessageParameter("fieldNames", String.join(", ", notNullGroup.fields()));

                // Convert the template
                hibernateContext.buildConstraintViolationWithTemplate(String.format(notNullGroup.message(), notNullGroup.type())) // the template is wrapped in {}, so it is looked up from the configuration file first, otherwise it is used as is
//                    .enableExpressionLanguage() // enables EL expression parsing for this custom error message. buildConstraintViolationWithTemplate does not enable EL by default
                    .addConstraintViolation();
            }
        }

        return isValid;
    }

    private boolean verify(Object[] objects) {
        switch (notNullGroup.type()) {
            case ALL_ARE_NULL:
                for (Object object : objects) {
                    if (object != null) return false;
                }
                return true;
            case ALL_ARE_NOT_NULL:
                for (Object object : objects) {
                    if (object == null) return false;
                }
                return true;
            case AT_LEAST_ONE_IS_NOT_NULL:
                for (Object object : objects) {
                    if (object != null) return true;
                }
                return false;
            case AT_LEAST_ONE_IS_NULL:
                for (Object object : objects) {
                    if (object == null) return true;
                }
                return false;
            case ONLY_ONE_IS_NOT_NULL: {
                int count = 0;
                for (Object object : objects) {
                    if (object != null) count++;
                }
                return count == 1;
            }
            case ONLY_ONE_IS_NULL: {
                int count = 0;
                for (Object object : objects) {
                    if (object == null) count++;
                }
                return count == 1;
            }
            case ALL_ARE_NOT_NULL_OR_ALL_ARE_NULL: {
                int count = 0;
                for (Object object : objects) {
                    if (object == null) count++;
                }
                return count == 0 || count == objects.length;
            }
            default:
                throw new IllegalStateException("Unsupported NotNullType: " + notNullGroup.type().name());
        }
    }

    /**
     * A simple LRU cache implementation, to prevent unbounded growth.
     */
    private static class LruExpressionCache extends ConcurrentHashMap<String, Expression> {
        private final int maxSize;

        LruExpressionCache(int maxSize) {
            this.maxSize = maxSize;
        }

        @Override
        public Expression put(String key, Expression value) {
            if (size() >= maxSize) {
                // Simple strategy: clear half (in real production, Caffeine or LinkedHashMap is recommended for a true LRU)
                clear();
            }
            return super.put(key, value);
        }
    }

}
