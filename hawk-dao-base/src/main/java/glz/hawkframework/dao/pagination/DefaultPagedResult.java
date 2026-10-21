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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class DefaultPagedResult<T> implements PagedResult<T> {

    private long totalCount = 0;
    private List<T> records = Collections.emptyList();
    private Page page;

    public DefaultPagedResult() {

    }

    public DefaultPagedResult(long totalCount, List<T> records) {
        setTotalCount(totalCount);
        setRecords(records);
    }

    public DefaultPagedResult(long totalCount, List<T> records, Page page) {
        setTotalCount(totalCount);
        setRecords(records);
        setPage(page);
    }

    @Nullable
    @Override
    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @Nonnull
    @Override
    public List<T> getRecords() {
        return records;
    }

    public void setRecords(List<T> records) {
        this.records = argNotNull(records, "records");
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = argument(totalCount, c -> c >= 0, c -> "totalCount must be greater than or equal to 0.");
    }
}
