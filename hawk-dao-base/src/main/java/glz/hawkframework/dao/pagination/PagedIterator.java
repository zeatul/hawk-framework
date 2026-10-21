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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>An {@link Iterator} implemented based on paged queries.</p>
 * <p>The records returned by {@code pagedRetriever} must strictly go beyond the boundary of the previous page
 * (for example, {@code where id > lastId order by id limit pageSize}); otherwise the iterator will throw
 * an {@link IllegalStateException} on the last page or when boundaries repeat, or stop iterating, in order to avoid an infinite loop.</p>
 *
 * @author Zhang Peng
 */
public class PagedIterator<K, V> implements Iterator<V> {

    /**
     * Used to cache paged records.
     */
    private List<V> pagedRecords = new ArrayList<>();
    private int pagedIndex = -1;
    private int maxPageSize = 0;
    private boolean lastPageReached = false;
    /**
     * Used to retrieve paged records.
     */
    private final Function<K, List<V>> pagedRetriever;
    /**
     * Used to obtain the comparison object for pagination from a record.
     */
    private final Function<V, K> compareObjectExtractor;

    /**
     * Constructs a pagination-based iterator.
     *
     * @param pagedRetriever         used to retrieve paged records
     * @param compareObjectExtractor    used to obtain the comparison object for pagination from a record
     */
    public PagedIterator(Function<K, List<V>> pagedRetriever, Function<V, K> compareObjectExtractor) {
        this.pagedRetriever = argNotNull(pagedRetriever, "pagedRetriever");
        this.compareObjectExtractor = argNotNull(compareObjectExtractor, "compareObjectExtractor");
    }

    @Override
    public boolean hasNext() {
        if (pagedIndex + 1 < pagedRecords.size()) {
            return true;
        }
        if (lastPageReached) {
            return false;
        }
        K compareObject = getCompareObject();
        List<V> nextPage = pagedRetriever.apply(compareObject);
        if (nextPage == null || nextPage.isEmpty()) {
            lastPageReached = true;
            pagedRecords = new ArrayList<>();
            pagedIndex = -1;
            return false;
        }
        if (maxPageSize > 0 && nextPage.size() < maxPageSize) {
            // The number of returned records is less than a full page, so this is the last page
            lastPageReached = true;
        } else if (isNoProgress(nextPage, compareObject)) {
            // pagedRetriever did not go beyond the previous boundary, so the query is not strictly monotonic;
            // continuing to iterate would cause an infinite loop
            lastPageReached = true;
        }
        maxPageSize = Math.max(maxPageSize, nextPage.size());
        pagedRecords = nextPage;
        pagedIndex = -1;
        return true;
    }

    private boolean isNoProgress(List<V> nextPage, K requestedCompareObject) {
        if (requestedCompareObject == null) return false;
        K lastCompareObject = compareObjectExtractor.apply(nextPage.get(nextPage.size() - 1));
        return lastCompareObject == null || requestedCompareObject.equals(lastCompareObject);
    }

    private K getCompareObject() {
        if (pagedRecords.isEmpty()) return null;
        return compareObjectExtractor.apply(pagedRecords.get(pagedRecords.size() - 1));
    }

    @Override
    public V next() {
        if (!hasNext()) throw new NoSuchElementException();
        return pagedRecords.get(++pagedIndex);
    }
}
