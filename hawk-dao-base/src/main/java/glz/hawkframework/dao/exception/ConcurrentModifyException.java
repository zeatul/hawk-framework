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

package glz.hawkframework.dao.exception;

import glz.hawkframework.core.helper.MapHelper;

import java.util.Map;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * Throws this exception while the record for deleting or modifying is changed by other process.
 *
 * @author Hawk
 */
public class ConcurrentModifyException extends DataAccessException {

    public final static String DEFAULT_ERROR_MESSAGE = "The record was modified of deleted by other process before.";

    public final static Function<Map<String, Object>, String> DEFAULT_MESSAGE_SUPPLIER = m -> DEFAULT_ERROR_MESSAGE;

    public ConcurrentModifyException(Function<Map<String, Object>, String> messageSupplier, Class<?> poClass, Map<String, Object> params) {
        super(messageSupplier == null ? DEFAULT_MESSAGE_SUPPLIER : messageSupplier,
            MapHelper.<String, Object>builder().put(argNotNull(params, "params").entrySet()).put(DEFAULT_KEY_PO_CLASS, poClass).build());
    }

    public ConcurrentModifyException(Function<Map<String, Object>, String> messageSupplier, Map<String, Object> params) {
        this(messageSupplier, null, params);
    }

    public ConcurrentModifyException(Class<?> poClass, Map<String, Object> params) {
        this(DEFAULT_MESSAGE_SUPPLIER, poClass, params);
    }

    public ConcurrentModifyException(Map<String, Object> params) {
        super(DEFAULT_MESSAGE_SUPPLIER, params);
    }
}
