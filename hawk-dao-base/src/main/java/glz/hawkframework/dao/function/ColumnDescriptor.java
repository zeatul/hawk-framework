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

package glz.hawkframework.dao.function;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class ColumnDescriptor<T> {
    private final String columnName;
    private final String fieldName;
    private final Supplier<T> columnSupplier;
    private final Supplier<Boolean> columnUpdatedSupplier;
    private final Consumer<T> columnModifier;

    public ColumnDescriptor(String columnName,String fieldName, Supplier<T> columnSupplier, Supplier<Boolean> columnUpdatedSupplier, Consumer<T> columnModifier) {
        this.columnName = columnName;
        this.fieldName = fieldName;
        this.columnSupplier = columnSupplier;
        this.columnUpdatedSupplier = columnUpdatedSupplier;
        this.columnModifier = columnModifier;
    }

    public String getColumnName() {
        return columnName;
    }

    public Supplier<T> getColumnSupplier() {
        return columnSupplier;
    }

    public Supplier<Boolean> getColumnUpdatedSupplier() {
        return columnUpdatedSupplier;
    }

    public Consumer<T> getColumnModifier() {
        return columnModifier;
    }

    public String getFieldName() {
        return fieldName;
    }
}


