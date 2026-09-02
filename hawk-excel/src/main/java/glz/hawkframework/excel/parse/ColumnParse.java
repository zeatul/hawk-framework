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

import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class ColumnParse {
    private int columnIndex;
    private BiConsumer<Object, Object> setter;
    private Function<Object, Object> getter;
    private Class<?> valueType;
    private String format;
    private String fieldName;
    private boolean isRowNo;
    private String columnName;
    private boolean readable;
    private boolean writable;

    public int getColumnIndex() {
        return this.columnIndex;
    }

    public void setColumnIndex(int columnIndex) {
        this.columnIndex = columnIndex;
    }

    public BiConsumer<Object, Object> getSetter() {
        return this.setter;
    }

    public void setSetter(BiConsumer<Object, Object> setter) {
        this.setter = setter;
    }

    public Function<Object, Object> getGetter() {
        return this.getter;
    }

    public void setGetter(Function<Object, Object> getter) {
        this.getter = getter;
    }

    public Class<?> getValueType() {
        return this.valueType;
    }

    public void setValueType(Class<?> valueType) {
        this.valueType = valueType;
    }

    public String getFormat() {
        return this.format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isRowNo() {
        return this.isRowNo;
    }

    public void setRowNo(boolean isRowNo) {
        this.isRowNo = isRowNo;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public boolean isReadable() {
        return this.readable;
    }

    public void setReadable(boolean readable) {
        this.readable = readable;
    }

    public boolean isWritable() {
        return this.writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
