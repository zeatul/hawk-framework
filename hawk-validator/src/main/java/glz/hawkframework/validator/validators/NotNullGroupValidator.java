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

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static glz.hawkframework.validator.constraints.NotNullGroup.DEFAULT_MESSAGE_TEMPLATE;

/**
 * 这个类用来校验一个对象的多个属性符合某种特定的非空条件
 *
 * @author Zhang Peng
 */
public class NotNullGroupValidator implements ConstraintValidator<NotNullGroup, Object> {

    private final ExpressionParser expressionParser = new SpelExpressionParser(new SpelParserConfiguration(SpelCompilerMode.MIXED,   // 编译模式
        null  // 类加载器，可为 null
    ));

    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<>();

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
            objects[i] = expression.getValue(value);
        }

        boolean isValid = verify(objects);

        if (!isValid) {
            if (notNullGroup.message().equals(DEFAULT_MESSAGE_TEMPLATE)) {
                context.disableDefaultConstraintViolation();

                // 转换为Hibernate特有的Context
                HibernateConstraintValidatorContext hibernateContext = context.unwrap(
                    HibernateConstraintValidatorContext.class
                );

                //添加动态变量供消息模板使用
//                hibernateContext.addExpressionVariable("fieldNames", String.join(", ", notNullGroup.fields())); // 只适用于EL
                hibernateContext.addMessageParameter("fieldNames", String.join(", ", notNullGroup.fields()));

                // 转换模板
                hibernateContext.buildConstraintViolationWithTemplate(String.format(notNullGroup.message(), notNullGroup.type())) // 模板用{}包起来，旧从配置文件找，否则就是本身
//                    .enableExpressionLanguage() //为当前这个自定义错误消息启用EL表达式解析能力。buildConstraintViolationWithTemplate默认不启用EL
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
            case AT_LEAST_ONE_IS_NO_NULL:
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

}
