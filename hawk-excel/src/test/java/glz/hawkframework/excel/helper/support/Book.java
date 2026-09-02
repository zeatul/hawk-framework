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

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
@ExcelSheet(sheetIndex = 0,sheetName = "图书信息")
public class Book {

    @ExcelColumn(isRowNo = true)
    private Integer rowNo;

    @ExcelColumn(index = 0, name = "书名")
    private String name;

    @ExcelColumn(index = 1, name = "单价")
    private BigDecimal price;

    @ExcelColumn(index = 2, name = "出版日期")
    private LocalDate publishDate;

    @ExcelColumn(index = 3, name = "是否有效")
    private Boolean valid;

    @ExcelColumn(index = 4, name = "数量")
    private Integer count;

    public Integer getRowNo() {
        return this.rowNo;
    }

    public void setRowNo(Integer rowNo) {
        this.rowNo = rowNo;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return this.price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getPublishDate() {
        return this.publishDate;
    }

    public void setPublishDate(LocalDate publishDate) {
        this.publishDate = publishDate;
    }

    public Boolean getValid() {
        return this.valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Integer getCount() {
        return this.count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
