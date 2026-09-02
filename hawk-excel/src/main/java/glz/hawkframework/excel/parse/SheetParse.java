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

package glz.hawkframework.excel.parse;

import java.util.List;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class SheetParse {
    private int sheetIndex;
    private String sheetName;
    private int dataStartIndex;
    private ColumnParse[] columns;
    private List<ColumnParse> rowNoColumns;
    /**
     * Sheet对应的接收Class类型
     */
    private Class<?> type;

    public int getSheetIndex() {
        return this.sheetIndex;
    }

    public void setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
    }

    public String getSheetName() {
        return this.sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public int getDataStartIndex() {
        return this.dataStartIndex;
    }

    public void setDataStartIndex(int dataStartIndex) {
        this.dataStartIndex = dataStartIndex;
    }

    public ColumnParse[] getColumns() {
        return this.columns;
    }

    public void setColumns(ColumnParse[] columns) {
        this.columns = columns;
    }

    public List<ColumnParse> getRowNoColumns() {
        return this.rowNoColumns;
    }

    public void setRowNoColumns(List<ColumnParse> rowNoColumns) {
        this.rowNoColumns = rowNoColumns;
    }

    public Class<?> getType() {
        return this.type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }
}
