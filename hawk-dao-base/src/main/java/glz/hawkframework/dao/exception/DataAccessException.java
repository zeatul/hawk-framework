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

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public abstract class DataAccessException extends RuntimeException {

    /**
     * The default key to find the po class. The po class is useful to find the corresponding table.
     */
    public final static String DEFAULT_KEY_PO_CLASS = "KEY_PO_CLASS";
    /**
     * the default key used in the params to find the inserted object.
     */
    public static final String DEFAULT_KEY_INSERTED_OBJECT = "KEY_INSERTED_OBJECT";
    /**
     * the default key used in the params to find the init cause.
     */
    public static final String DEFAULT_KEY_INIT_CAUSE = "KEY_INIT_CAUSE";

    private final Map<String, Object> params;
    private final Function<Map<String, Object>, String> messageSupplier;

    protected DataAccessException(Function<Map<String, Object>, String> messageSupplier, Map<String, Object> params) {
        super(argNotNull(messageSupplier, "messageSupplier").apply(argNotNull(params, "params")));
        this.params = params;
        this.messageSupplier = messageSupplier;
    }

    public Map<String, Object> getParams() {
        return Collections.unmodifiableMap(params);
    }

    public Function<Map<String, Object>, String> getMessageSupplier() {
        return messageSupplier;
    }
}
