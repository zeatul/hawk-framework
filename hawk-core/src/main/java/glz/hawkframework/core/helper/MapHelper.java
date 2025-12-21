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

package glz.hawkframework.core.helper;

import java.util.*;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */

public class MapHelper {


    @SafeVarargs
    public static <K, V> Map<K, V> ofMap(Map.Entry<K, V>... entries) {
        return ofHashMap(entries);
    }

    @SafeVarargs
    public static <K, V> HashMap<K, V> ofHashMap(Map.Entry<K, V>... entries) {
        return  MapHelper.<K,V>builder().put(entries).buildHashMap();
    }

    @SafeVarargs
    public static <K, V> LinkedHashMap<K, V> ofLinkedHashMap(Map.Entry<K, V>... entries) {
        return MapHelper.<K,V>builder().put(entries).buildLinkedHashMap();
    }

    public static <K, V> Builder<K, V> builder() {
        return new Builder<K, V>();
    }

    public static <K, V> Builder<K, V> mapBuilder() {
        return new Builder<K, V>();
    }

    public static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return new EntryHolder<>(k, v);
    }

    public final static class EntryHolder<K, V> implements Map.Entry<K, V> {
        private final K k;
        private final V v;

        private EntryHolder(K k, V v) {
            this.k = argNotNull(k, "k");
            this.v = argNotNull(v, "v");
        }

        @Override
        public K getKey() {
            return k;
        }

        @Override
        public V getValue() {
            return v;
        }

        @Override
        public V setValue(V value) {
            throw new UnsupportedOperationException();
        }
    }

    public static class Builder<K, V> {
        private final LinkedHashMap<K, V> m = new LinkedHashMap<>();

        public Builder<K, V> put(Map.Entry<K, V> entry) {
            argNotNull(entry, "entry");
            m.put(entry.getKey(), entry.getValue());
            return this;
        }

        @SafeVarargs
        public final Builder<K, V> put(Map.Entry<K, V>... entries) {
            Arrays.stream(argNotNull(entries, "entries")).forEach(this::put);
            return this;
        }

        public Builder<K, V> put(Collection<Map.Entry<K, V>> entries) {
            argNotNull(entries, "entries").forEach(this::put);
            return this;
        }

        public Builder<K, V> put(K k, V v) {
            m.put(k, v);
            return this;
        }

        public Builder<K, V> putAll(Map<K, V> map) {
            m.putAll(map);
            return this;
        }

        public HashMap<K, V> build() {
            return buildHashMap();
        }

        public HashMap<K, V> buildHashMap() {
            return new HashMap<>(m);
        }

        public LinkedHashMap<K, V> buildLinkedHashMap() {
            return new LinkedHashMap<>(m);
        }

        public Map<K, V> build(Class<? extends Map<K, V>> mapClass) {
            try {
                Map<K, V> mm = mapClass.newInstance();
                mm.putAll(m);
                return mm;

            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
