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

package glz.hawkframework.dao.process;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class UpdateParameter {
    public final Object recordVersion;
    public final Object fieldUpdateObject;
    public final Object columnUpdateObject;

    /**
     * The params which is used in query conditions.
     */
    public final Map<String,Object> params;

    private UpdateParameter(Builder builder) {
        this.recordVersion = builder.recordVersion;
        this.fieldUpdateObject = builder.fieldUpdateObject;
        this.columnUpdateObject = builder.columnUpdateObject;
        this.params = Collections.unmodifiableMap(builder.params);
    }

    public static Builder builder() {
        return new Builder();
    }


    public static class Builder {
        private Object recordVersion;
        private Object fieldUpdateObject;
        private Object columnUpdateObject;
        private final Map<String,Object> params = new HashMap<>();

        private Builder() {
        }

        public Builder setRecordVersion(Object recordVersion) {
            this.recordVersion = recordVersion;
            return this;
        }

        public Builder setFieldUpdateObject(Object fieldUpdateObject) {
            this.fieldUpdateObject = fieldUpdateObject;
            this.columnUpdateObject = null;
            return this;
        }

        public Builder setColumnUpdateObject(Object columnUpdateObject) {
            this.columnUpdateObject = columnUpdateObject;
            this.fieldUpdateObject = null;
            return this;
        }

        public Builder addParams(Map<String,Object> params) {
            this.params.putAll(argNotNull(params, "params"));
            return this;
        }

        public Builder addParam(String key, Object value) {
            this.params.put(argNotNull(key, "key"), value);
            return this;
        }

        public UpdateParameter build() {
            return new UpdateParameter(this);
        }
    }

}
