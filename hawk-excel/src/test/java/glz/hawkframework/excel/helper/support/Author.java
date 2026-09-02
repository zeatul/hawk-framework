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

package glz.hawkframework.excel.helper.support;

import glz.hawkframework.excel.annotations.ExcelColumn;
import glz.hawkframework.excel.annotations.ExcelSheet;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
@ExcelSheet(sheetIndex = 1)
public class Author {
    @ExcelColumn(index = 0)
    private String authorName;
    @ExcelColumn(index = 1)
    private Integer authorAge;

    public String getAuthorName() {
        return this.authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public Integer getAuthorAge() {
        return this.authorAge;
    }

    public void setAuthorAge(Integer authorAge) {
        this.authorAge = authorAge;
    }
}
