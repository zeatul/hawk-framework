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
 * <p>Fields annotated with {@code ExcelColumn} are responsible for receiving the data of the column at the specified index</p>
 *
 * @author Zhang Peng
 */
@Target({FIELD})
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelColumn {

    /**
     * The column index of the Excel column
     * <p>The index starts from {@code 0}</p>
     */
    int index() default  -1;

    /**
     * The name of the Excel column
     * <p>When exporting, it can be used as the header</p>
     * <p>When reading a file and only the data content is needed, it may be empty</p>
     */
    String name() default "";

    /**
     * The value of this attribute is used to provide the format string
     */
    String format() default "";

    /**
     * Whether it is a row number
     * <p>If the value of this attribute is {@code true}, it means this column is a row number and the value of {@link #index()} is ignored</p>
     * <p>The default value is {@code false}</p>
     * <p>The row number starts from {@code 0}</p>
     */
    boolean isRowNo() default false;

    /**
     * Whether the corresponding excel column is readable
     */
    boolean readable() default true;

    /**
     * Whether the corresponding excel column is writable
     */
    boolean writable() default true;
}
