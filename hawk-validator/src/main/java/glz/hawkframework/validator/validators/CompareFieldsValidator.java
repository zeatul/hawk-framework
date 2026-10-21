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

import glz.hawkframework.validator.constraints.CompareFields;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.math.BigDecimal;
import java.time.*;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.validator.constraints.CompareFields.DEFAULT_MESSAGE_TEMPLATE;

/**
 * This class is used to validate that the comparison result of two fields of an object matches the expectation.
 *
 * @author Zhang Peng
 */
public class CompareFieldsValidator<T> implements ConstraintValidator<CompareFields, T> {

    private final ExpressionParser expressionParser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED,   // compiler mode
        null  // class loader, may be null
    ));

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

    // Upper limit of the SpEL expression cache, to prevent OOM caused by dynamic field paths
    private static final int MAX_EXPRESSION_CACHE_SIZE = 1024;

    private CompareFields compareFields;

    @Override
    public void initialize(CompareFields constraintAnnotation) {
        this.compareFields = constraintAnnotation;
    }

    @Override
    public boolean isValid(T value, ConstraintValidatorContext context) {
        if (value == null) return true;
        final String left = argNotBlank(compareFields.left(), "CompareFields.left");
        final String right = argNotBlank(compareFields.right(), "CompareFields.right");
        final CompareFields.CompareResult expectedResult = compareFields.expectedResult();
        final Expression expForLeft = getCachedExpression(left);
        final Object leftValue = expForLeft.getValue(value);
        if (leftValue == null) return true;
        final Expression expForRight = getCachedExpression(right);
        final Object rightValue = expForRight.getValue(value);
        if (rightValue == null) return true;

        boolean isValid = match(expectedResult, compare(leftValue, rightValue));
        if (!isValid) {
            if (compareFields.message().equals(DEFAULT_MESSAGE_TEMPLATE)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(String.format(compareFields.message(), expectedResult.name())).addConstraintViolation();
            }
        }

        return isValid;
    }

    private Expression getCachedExpression(String expressionString) {
        if (expressionCache.size() >= MAX_EXPRESSION_CACHE_SIZE) {
            expressionCache.clear();
        }
        return expressionCache.computeIfAbsent(expressionString, expressionParser::parseExpression);
    }

    private boolean match(CompareFields.CompareResult expectedResult, int actualResult) {
        switch (expectedResult) {
            case LESS:
                return actualResult < 0;
            case LESS_OR_EQUAL:
                return actualResult <= 0;
            case GREATER:
                return actualResult > 0;
            case GREATER_OR_EQUAL:
                return actualResult >= 0;
            case EQUAL:
                return actualResult == 0;
            case NOT_EQUAL:
                return actualResult != 0;
            default:
                throw new IllegalStateException("Unsupported CompareResult: " + expectedResult.name());
        }
    }

    @SuppressWarnings("unchecked")
    private int compare(final Object leftValue, final Object rightValue) {
        if (leftValue == rightValue) return 0;
        // 1. Same type and Comparable
        if (leftValue.getClass() == rightValue.getClass() && leftValue instanceof Comparable) {
            return ((Comparable<Object>) leftValue).compareTo(rightValue);
        }

        // 2. Numeric types are uniformly converted to BigDecimal (supports cross-type: Long vs Integer, etc.)
        if (leftValue instanceof Number && rightValue instanceof Number) {
            BigDecimal aNum = toBigDecimal((Number) leftValue);
            BigDecimal bNum = toBigDecimal((Number) rightValue);
            return aNum.compareTo(bNum);
        }

        // 3. Date/time types are uniformly converted to Instant (supports Date and the java.time family)
        // Note: Long is no longer automatically treated as a timestamp, to avoid business IDs/version numbers being misidentified
        if (isTemporalType(leftValue) && isTemporalType(rightValue)) {
            Instant aInst = toInstant(leftValue);
            Instant bInst = toInstant(rightValue);
            return aInst.compareTo(bInst);
        }

        // 4. String comparison
        if (leftValue instanceof CharSequence && rightValue instanceof CharSequence) {
            return leftValue.toString().compareTo(rightValue.toString());
        }

        // 5. Other cases cannot be compared
        throw new IllegalArgumentException(String.format("Can't compare %s with %s. Supported: same-type Comparable, Number, Temporal, CharSequence", leftValue.getClass().getName(), rightValue.getClass().getName()));
    }

    private static BigDecimal toBigDecimal(Number num) {
        if (num instanceof BigDecimal) return (BigDecimal) num;
        if (num instanceof Double || num instanceof Float) {
            // Avoid floating-point precision errors, construct from a string
            return new BigDecimal(num.toString());
        }
        return BigDecimal.valueOf(num.longValue());
    }

    /**
     * Determines whether the value is a temporal type.
     * <p>Does not include {@code Long}, to avoid business fields (IDs, version numbers) being misidentified as timestamps.</p>
     * <p>Does not include {@code LocalTime}, because it cannot be converted to an {@link Instant} (it has no date information);
     * {@code false} is returned here so that it goes through the subsequent string/other comparison branches, avoiding an exception.</p>
     */
    private static boolean isTemporalType(Object obj) {
        if (obj instanceof LocalTime) return false;
        return obj instanceof Date || obj instanceof Temporal;      // LocalDate, LocalDateTime, ZonedDateTime, etc.
    }

    private static Instant toInstant(Object obj) {
        if (obj instanceof Date) {
            return ((Date) obj).toInstant();
        }
        if (obj instanceof LocalDate) {
            return ((LocalDate) obj).atStartOfDay(ZoneId.systemDefault()).toInstant();
        }
        if (obj instanceof LocalDateTime) {
            return ((LocalDateTime) obj).atZone(ZoneId.systemDefault()).toInstant();
        }
        if (obj instanceof ZonedDateTime) {
            return ((ZonedDateTime) obj).toInstant();
        }
        if (obj instanceof Instant) {
            return (Instant) obj;
        }
        throw new IllegalArgumentException("Unsupported time type: " + obj.getClass());
    }

    public static class CompareFieldsValidatorForObject extends CompareFieldsValidator<Object> {
    }
}
