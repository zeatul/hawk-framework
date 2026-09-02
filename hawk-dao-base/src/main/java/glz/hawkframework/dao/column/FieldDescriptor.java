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

package glz.hawkframework.dao.column;

import java.lang.annotation.Annotation;
import java.util.function.BiConsumer;
import java.util.function.Function;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class FieldDescriptor<K, V, Z extends Annotation> {
    private final String fieldName;
    private final Function<K, V> fieldSupplier;
    private final Function<K, Boolean> fieldUpdatedSupplier;
    private final BiConsumer<K, V> fieldModifier;
    private final Z fieldAnnotation;

    public FieldDescriptor(String fieldName, Z fieldAnnotation,Function<K, V> fieldSupplier, Function<K, Boolean> fieldUpdatedSupplier,BiConsumer<K, V> fieldModifier) {
        this.fieldName = fieldName;
        this.fieldAnnotation = fieldAnnotation;
        this.fieldSupplier = fieldSupplier;
        this.fieldUpdatedSupplier = fieldUpdatedSupplier;
        this.fieldModifier = fieldModifier;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Function<K, V> getFieldSupplier() {
        return fieldSupplier;
    }

    public Function<K, Boolean> getFieldUpdatedSupplier() {
        return fieldUpdatedSupplier;
    }

    public BiConsumer<K, V> getFieldModifier() {
        return fieldModifier;
    }

    public Z getFieldAnnotation() {
        return fieldAnnotation;
    }
}



