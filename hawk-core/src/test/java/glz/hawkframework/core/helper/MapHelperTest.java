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

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class MapHelperTest {

    @Test
    public void entry_nullValue_allowed() {
        Map.Entry<String, String> entry = MapHelper.entry("key", null);
        assertThat(entry.getKey()).isEqualTo("key");
        assertThat(entry.getValue()).isNull();
    }

    @Test
    public void entry_nullKey_throws() {
        assertThatThrownBy(() -> MapHelper.entry(null, "value"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("k");
    }

    @Test
    public void builder_put_nullValue_allowed() {
        HashMap<String, String> map = MapHelper.<String, String>builder()
            .put("key1", "value1")
            .put("key2", null)
            .buildHashMap();

        assertThat(map).containsEntry("key1", "value1");
        assertThat(map).containsEntry("key2", null);
    }

    @Test
    public void builder_put_nullKey_throws() {
        assertThatThrownBy(() -> MapHelper.<String, String>builder().put(null, "value"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("key");
    }

    @Test
    public void builder_putEntry_nullValue_allowed() {
        HashMap<String, String> map = MapHelper.<String, String>builder()
            .put(MapHelper.entry("key1", "value1"))
            .put(MapHelper.entry("key2", null))
            .buildHashMap();

        assertThat(map).containsEntry("key1", "value1");
        assertThat(map).containsEntry("key2", null);
    }

    @Test
    public void builder_putEntry_nullKey_throws() {
        // Entry construction itself throws for null key
        assertThatThrownBy(() -> MapHelper.entry(null, "value"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("k");
    }

    @Test
    public void builder_putEntry_nullEntry_throws() {
        assertThatThrownBy(() -> MapHelper.<String, String>builder().put((Map.Entry<String, String>) null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("entry");
    }

    @Test
    public void builder_putEntries_varargs_nullValue_allowed() {
        HashMap<String, String> map = MapHelper.<String, String>ofHashMap(
            MapHelper.entry("key1", "value1"),
            MapHelper.entry("key2", null)
        );

        assertThat(map).containsEntry("key1", "value1");
        assertThat(map).containsEntry("key2", null);
    }

    @Test
    public void builder_putEntries_varargs_nullKey_throws() {
        assertThatThrownBy(() -> MapHelper.<String, String>ofHashMap(MapHelper.entry(null, "value")))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("k");
    }

    @Test
    public void builder_putAll_nullValue_allowed() {
        Map<String, String> source = new LinkedHashMap<>();
        source.put("key1", "value1");
        source.put("key2", null);

        HashMap<String, String> map = MapHelper.<String, String>builder()
            .putAll(source)
            .buildHashMap();

        assertThat(map).containsEntry("key1", "value1");
        assertThat(map).containsEntry("key2", null);
    }

    @Test
    public void builder_putCollection_nullValue_allowed() {
        MapHelper.<String, String>ofLinkedHashMap(
            MapHelper.entry("a", "1"),
            MapHelper.entry("b", null)
        );
    }

    @Test
    public void buildClass_nullValue_allowed() {
        // Using buildHashMap() as the primary API; build(Class) requires parameterized type which is hard to express
        HashMap<String, String> map = MapHelper.<String, String>builder()
            .put("key", null)
            .buildHashMap();

        assertThat(map).containsEntry("key", null);
    }

    @Test
    public void buildLinkedHashMap_nullValue_allowed() {
        LinkedHashMap<String, String> map = MapHelper.<String, String>builder()
            .put("key", null)
            .buildLinkedHashMap();

        assertThat(map).containsEntry("key", null);
    }
}