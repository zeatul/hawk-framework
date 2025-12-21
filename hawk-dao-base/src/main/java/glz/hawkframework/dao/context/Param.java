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

package glz.hawkframework.dao.context;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class Param {
    private final String paramName;
    private final Class<?> paramClass;
    private final Object paramValues;

    public Param(String paramName, Class<?> paramClass, Object paramValues) {
        this.paramName = argNotBlank(paramName,"paramName");
        this.paramClass = argNotNull(paramClass,"paramClass");
        this.paramValues = paramValues;
    }

    public Param(String paramName,Object paramValue){
        this(paramName,argNotNull(paramValue,"paramValue").getClass(),paramValue);
    }

    public String getParamName() {
        return paramName;
    }

    public Class<?> getParamClass() {
        return paramClass;
    }

    public Object getParamValues() {
        return paramValues;
    }
}
