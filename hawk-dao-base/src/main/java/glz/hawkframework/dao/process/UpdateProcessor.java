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

/**
 * This interface is responsible for
 *
 * @author Zhang Peng
 */
public interface UpdateProcessor {

//    public final static String RECORD_VERSION = "UPDATE_PROCESSOR_RECORD_VERSION";
//    public final static String UPDATE_BY_FIELD_OBJECT = "UPDATE_PROCESSOR_UPDATE_BY_FIELD_OBJECT";
//    public final static String UPDATE_BY_COLUMN_OBJECT = "UPDATE_PROCESSOR_UPDATE_BY_COLUMN_OBJECT";
//    public final static String RECORD_ID = "UPDATE_PROCESSOR_RECORD_ID";

    /**
     * Execute before update
     *
     * @param updateParameter contains all the parameters of the update method
     */
    default void beforeUpdate(UpdateParameter updateParameter) {
    }

    /**
     * Execute after update
     *
     * @param updateParameter contains all the parameters of the update method
     */
    default void afterUpdate(UpdateParameter updateParameter) {
    }
}
