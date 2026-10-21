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
 * <p>Classes annotated with {@code ExcelSheet} are responsible for receiving the data of the sheet at the specified index</p>
 *
 * @author Zhang Peng
 */
@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface ExcelSheet {
    /**
     * The index of the Excel sheet
     * <p>The sheet index starts from {@code 0}</p>
     */
    int sheetIndex() default 0;

    /*
     * The name of the Excel sheet
     * <p>When exporting, it is used to specify the name of the sheet; if empty, the default Excel name is used</p>
     */
    String sheetName() default "";

    /**
     * The start row index of the data records of the Excel sheet
     * <p>The start row index starts from {@code 0}</p>
     * <p>The default value is {@code 1}, assuming that the first row of the sheet is the title row</p>
     */
    int dataStartIndex() default 1;
}
