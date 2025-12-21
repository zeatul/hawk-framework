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

package glz.hawkframework.dao.pagination;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultRowNoRange implements RowNoRange {
    private long minRowNo = 1;
    private long maxRowNo = 2;

    public DefaultRowNoRange() {
    }

    public DefaultRowNoRange(long minRowNo, long maxRowNo) {
        setMinRowNo(minRowNo);
        setMaxRowNo(maxRowNo);
    }

    @Override
    public long getMinRowNo() {
        return minRowNo;
    }

    public void setMinRowNo(long minRowNo) {
        if (minRowNo < 1) {
            throw new IllegalArgumentException("minRowNo must be greater than 1.");
        }
        this.minRowNo = minRowNo;
    }

    @Override
    public long getMaxRowNo() {
        if (maxRowNo <= minRowNo) {
            throw new IllegalStateException("maxRowNo must be greater than minRowNo.");
        }
        return maxRowNo;
    }

    public void setMaxRowNo(long maxRowNo) {
        if (maxRowNo <= minRowNo) {
            throw new IllegalArgumentException("maxRowNo must be greater than minRowNo.");
        }
        this.maxRowNo = maxRowNo;
    }
}
