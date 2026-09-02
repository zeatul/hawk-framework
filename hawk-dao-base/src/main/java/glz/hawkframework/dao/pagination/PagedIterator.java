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
import java.util.function.Function;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>基于分页查询实现的{@link Iterator}</p>
 *
 * @author Zhang Peng
 */
public class PagedIterator<K, V> implements Iterator<V> {

    /**
     * 用来缓存分页记录
     */
    private List<V> pagedRecords = new ArrayList<>();
    private int pagedIndex = -1;
    /**
     * 用来获取分页记录
     */
    private final Function<K, List<V>> pagedRetriever;
    /**
     * 用来从记录里获取分页用的比较对象
     */
    private final Function<V, K> compareObjectExtractor;

    /**
     * 构造基于分页的迭代器
     *
     * @param pagedRetriever 用来获取分页记录
     * @param compareObjectExtractor    用来从记录里获取分页用的比较对象
     */
    public PagedIterator(Function<K, List<V>> pagedRetriever, Function<V, K> compareObjectExtractor) {
        this.pagedRetriever = argNotNull(pagedRetriever,"pagedRetriever");
        this.compareObjectExtractor = argNotNull(compareObjectExtractor,"compareObjectExtractor");
    }

    @Override
    public boolean hasNext() {
        if (pagedRecords.size() > pagedIndex + 1) {
            return true;
        } else {
            pagedRecords = pagedRetriever.apply(getCompareObject());
            pagedIndex = -1;
            return !pagedRecords.isEmpty();
        }
    }

    private K getCompareObject(){
        if (pagedRecords.isEmpty()) return  null;
        return compareObjectExtractor.apply(pagedRecords.get(pagedRecords.size()-1));
    }

    @Override
    public V next() {
        return pagedRecords.get(++pagedIndex);
    }
}
