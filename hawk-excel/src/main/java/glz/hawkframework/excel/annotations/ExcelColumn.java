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

package glz.hawkframework.excel.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.FIELD;

/**
 * This annotation is responsible for
 * <p>被{@code ExcelColumn}注解的字段，负责接收指定索引所在的column的数据</p>
 *
 * @author Zhang Peng
 */
@Target({FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    /**
     * Excel的列索引
     * <p>索引号从{@code 0}开始计算</p>
     */
    int index() default  -1;

    /**
     * Excel的Column名称
     * <p>导出时，可以作为表头</p>
     * <p>读文件，只需要取数据内容时，可以为空</p>
     */
    String name() default "";

    /**
     * 该属性的值用来提供格式化字符串
     */
    String format() default "";

    /**
     * 是否是行号
     * <p>如果这个属性的值为{@code true}，则表示该列是行号，忽略{@link #index()}的值</p>
     * <p>默认值为{@code false}</p>
     * <p>行号从{@code 0}开始计算</p>
     */
    boolean isRowNo() default false;

    /**
     * 对应的excel column是否可读
     */
    boolean readable() default true;

    /**
     * 对应的excel column是否可写
     */
    boolean writable() default true;
}
