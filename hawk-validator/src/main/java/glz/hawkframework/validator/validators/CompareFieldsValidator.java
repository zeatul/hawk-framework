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
 * 这个类用来校验一个对象的两个属性的比较结果符合预期
 *
 * @author Zhang Peng
 */
public class CompareFieldsValidator<T> implements ConstraintValidator<CompareFields, T> {

    private final ExpressionParser expressionParser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED,   // 编译模式
        null  // 类加载器，可为 null
    ));

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

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
        final Expression expForLeft = expressionCache.computeIfAbsent(left, expressionParser::parseExpression);
        final Object leftValue = expForLeft.getValue(value);
        if (leftValue == null) return true;
        final Expression expForRight = expressionCache.computeIfAbsent(right, expressionParser::parseExpression);
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
        // 1. 相同类型且为 Comparable
        if (leftValue.getClass() == rightValue.getClass() && leftValue instanceof Comparable) {
            return ((Comparable<Object>) leftValue).compareTo(rightValue);
        }

        // 2. 数字类型统一转 BigDecimal
        if (leftValue instanceof Number && rightValue instanceof Number) {
            BigDecimal aNum = toBigDecimal((Number) leftValue);
            BigDecimal bNum = toBigDecimal((Number) rightValue);
            return aNum.compareTo(bNum);
        }

        // 3. 日期时间类型统一转 Instant（支持 Date 及 java.time 系列）
        if (isTemporalType(leftValue) && isTemporalType(rightValue)) {
            Instant aInst = toInstant(leftValue);
            Instant bInst = toInstant(rightValue);
            return aInst.compareTo(bInst);
        }

        // 4. 字符串比较
        if (leftValue instanceof CharSequence && rightValue instanceof CharSequence) {
            return leftValue.toString().compareTo(rightValue.toString());
        }

        // 5. 其他情况（比如 LocalDate vs String）无法比较
        throw new IllegalArgumentException(String.format("Can't compare %s with %s", leftValue.getClass().getName(), rightValue.getClass().getName()));
    }

    private static BigDecimal toBigDecimal(Number num) {
        if (num instanceof BigDecimal) return (BigDecimal) num;
        if (num instanceof Double || num instanceof Float) {
            // 避免浮点精度误差，使用字符串构造
            return new BigDecimal(num.toString());
        }
        return BigDecimal.valueOf(num.longValue());
    }

    private static boolean isTemporalType(Object obj) {
        return obj instanceof Date || obj instanceof Temporal ||      // LocalDate, LocalDateTime, ZonedDateTime 等
            obj instanceof Long;            // 时间戳（毫秒）也可视为时间
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
        if (obj instanceof Long) {
            return Instant.ofEpochMilli((Long) obj);
        }
        throw new IllegalArgumentException("Unsupported time type: " + obj.getClass());
    }

    public static class CompareFieldsValidatorForObject extends CompareFieldsValidator<Object> {
    }
}
