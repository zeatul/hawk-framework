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

package glz.hawkframework.dao.sharding;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class ShardingParam {
    /**
     * Table name
     */
    private final String tableName;
    /**
     * Name of the column used to generate the sharding key
     */
    private final String columnName;

    public ShardingParam(String tableName, String columnName, Object columnValue) {
        this.tableName = argNotBlank(tableName, "tableName");
        this.columnName = argNotBlank(columnName, "columnName");
        this.columnValue = argNotNull(columnValue, "columnValue");
    }

    /**
     * Column value used to generate the sharding key
     */
    private final Object columnValue;

    public String getTableName() {
        return this.tableName;
    }

    public String getColumnName() {
        return this.columnName;
    }

    public Object getColumnValue() {
        return this.columnValue;
    }
}
