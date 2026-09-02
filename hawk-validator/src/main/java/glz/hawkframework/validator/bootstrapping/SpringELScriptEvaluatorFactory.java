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

package glz.hawkframework.validator.bootstrapping;

import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.hibernate.validator.constraints.ScriptAssert;
import org.hibernate.validator.spi.scripting.AbstractCachingScriptEvaluatorFactory;
import org.hibernate.validator.spi.scripting.ScriptEvaluationException;
import org.hibernate.validator.spi.scripting.ScriptEvaluator;
import org.hibernate.validator.spi.scripting.ScriptEvaluatorNotFoundException;
import org.springframework.expression.*;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import javax.validation.Validator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * 这个类用来支持Spel表达式校验
 * <p>用来支持{@link ScriptAssert @ScriptAssert}和{@link ParameterScriptAssert @ParameterScriptAssert}</p>
 * <p>需要给{@link Validator}注册脚本执行器</p>
 * <pre>
 *     public static Validator createValidator() {
 *         return Validation.byProvider(HibernateValidator.class)
 *                 .configure()
 *                 // 注册你的 SpringELScriptEvaluatorFactory
 *                 .scriptEvaluatorFactory(new SpringELScriptEvaluatorFactory())
 *                 .buildValidatorFactory()
 *                 .getValidator();
 *     }
 * </pre>
 *
 * @author Zhang Peng
 */
public class SpringELScriptEvaluatorFactory extends AbstractCachingScriptEvaluatorFactory {

    @Override
    protected ScriptEvaluator createNewScriptEvaluator(String languageName) throws ScriptEvaluatorNotFoundException {
        if (!"spring".equalsIgnoreCase(languageName)) {
            throw new IllegalStateException("Only Spring EL is supported");
        }

        return new SpringELScriptEvaluator();
    }

    private static class SpringELScriptEvaluator implements ScriptEvaluator {

        private final ExpressionParser expressionParser = new SpelExpressionParser();

        @Override
        public Object evaluate(String script, Map<String, Object> bindings) throws ScriptEvaluationException {
            try {
                Expression expression = expressionParser.parseExpression(script);
                EvaluationContext context = new StandardEvaluationContext(bindings.values().iterator().next());
                for (Entry<String, Object> binding : bindings.entrySet()) {
                    context.setVariable(binding.getKey(), binding.getValue());
                }
                return expression.getValue(context);
            } catch (ParseException | EvaluationException e) {
                throw new ScriptEvaluationException("Unable to evaluate SpEL script", e);
            }
        }
    }
}
