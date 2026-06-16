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

import static glz.hawkframework.core.support.ArgumentSupport.GREATER_THAN_OR_EQUAL_TO;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultPage implements Page {

    private long pageIndex = 0;

    private long pageSize = 1;

    public DefaultPage() {
    }

    /**
     * Creates a {@link Page} instance.
     *
     * @param pageIndex the index of page. The index starts from {@code 1}
     * @param pageSize  the size of page. It must be greater than zero.
     */
    public DefaultPage(long pageIndex, long pageSize) {
        setPageIndex(pageIndex);
        setPageSize(pageSize);
    }

    /**
     * Obtains a {@link Page} instance.
     *
     * @param pageIndex the index of page. The index starts from {@code 1}
     * @param pageSize  the size of page. It must be greater than zero.
     * @return a {@link Page} instance
     */
    public static DefaultPage page(long pageIndex, long pageSize) {
        return new DefaultPage(pageIndex, pageSize);
    }

    @Override
    public long getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(long pageIndex) {
        this.pageIndex = argument(pageIndex, index -> index >= 1L,
            index -> String.format(GREATER_THAN_OR_EQUAL_TO, "pageIndex", 1));

    }

    @Override
    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = argument(pageSize, size -> size >= 1L,
            index -> String.format(GREATER_THAN_OR_EQUAL_TO, "pageSize", 1));
    }

    @Override
    public OffsetAndLimit toOffsetAndLimit() {
        DefaultOffsetAndLimit defaultOffsetAndLimit = new DefaultOffsetAndLimit();
        defaultOffsetAndLimit.setOffset((pageIndex - 1) * pageSize);
        defaultOffsetAndLimit.setLimit(pageSize);
        return defaultOffsetAndLimit;
    }

    @Override
    public RowNoRange toRowNoRage() {
        DefaultRowNoRange defaultRowNoRange = new DefaultRowNoRange();
        defaultRowNoRange.setMinRowNo((pageIndex - 1) * pageSize + 1);
        defaultRowNoRange.setMaxRowNo(pageIndex * pageSize);
        return defaultRowNoRange;
    }
}
