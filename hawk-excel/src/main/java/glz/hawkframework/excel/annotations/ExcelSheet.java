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

import java.lang.annotation.*;

/**
 * This annotation is responsible for
 * <p>被{@code ExcelSheet}注解的类，负责接收指定索引所在的sheet的数据</p>
 *
 * @author Zhang Peng
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSheet {
    /**
     * Excel Sheet的索引号
     * <p>Sheet的索引号从{@code 0}开始</p>
     */
    int sheetIndex() default 0;

    /*
     * Excel Sheet的名称
     * <p>导出时，用来指定sheet的名称，为空，则使用Excel的默认名称</p>
     */
    String sheetName() default "";

    /**
     * Excel Sheet的数据记录的起始行号
     * <p>起始行号从{@code 0}开始</p>
     * <p>默认值为{@code 1}，假设该sheet的第一行为标题行</p>
     */
    int dataStartIndex() default 1;
}
