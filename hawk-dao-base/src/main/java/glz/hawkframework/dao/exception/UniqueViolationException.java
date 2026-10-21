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
 * Throws this exception while the record is in the database and can't be inserted.
 *
 * @author Zhang Peng
 */
public class UniqueViolationException extends DataAccessException {

    public final static String DEFAULT_ERROR_MESSAGE = "The record with the same primary key or unique index was inserted before.";

    public final static Function<Map<String, Object>, String> DEFAULT_MESSAGE_SUPPLIER = params -> DEFAULT_ERROR_MESSAGE;

    public UniqueViolationException(Throwable initCause, Function<Map<String, Object>, String> messageSupplier, Class<?> poClass, Map<String, Object> params) {
        super(messageSupplier == null ? DEFAULT_MESSAGE_SUPPLIER : messageSupplier,
            MapHelper.<String, Object>builder().put(argNotNull(params, "params").entrySet())
                .put(DEFAULT_KEY_PO_CLASS, argNotNull(poClass, "poClass"))
                .put(DEFAULT_KEY_INIT_CAUSE, argNotNull(initCause, "initCause"))
                .build());
        this.initCause(initCause);
    }

    public UniqueViolationException(Throwable initCause, Function<Map<String, Object>, String> messageSupplier, Map<String, Object> params) {
        this(initCause, messageSupplier, null, params);
    }

    public UniqueViolationException(Throwable initCause, Class<?> poClass, Map<String, Object> params) {
        this(initCause, DEFAULT_MESSAGE_SUPPLIER, poClass, params);
    }

    public UniqueViolationException(Throwable initCause, Map<String, Object> params) {
        this(initCause, DEFAULT_MESSAGE_SUPPLIER, params);
    }


}
