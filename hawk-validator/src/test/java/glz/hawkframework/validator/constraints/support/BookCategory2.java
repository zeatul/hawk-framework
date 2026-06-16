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

package glz.hawkframework.validator.constraints.support;

import java.util.Map;

import static glz.hawkframework.core.helper.MapHelper.entry;
import static glz.hawkframework.core.helper.MapHelper.ofMap;
import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;

/**
 * This enum is responsible for
 *
 * @author Zhang Peng
 */
public enum BookCategory2 {
    MATH("1"), ENGLISH("2"), CHINESE("3");
    public final String value;

    BookCategory2(String value) {
        this.value = value;
    }

    private final static Map<String, BookCategory2> valueMap = ofMap(entry(MATH.value, MATH), entry(ENGLISH.value, ENGLISH), entry(CHINESE.value, CHINESE));

    private final static Map<String, BookCategory2> nameMap = ofMap(entry(MATH.name(), MATH), entry(ENGLISH.name(), ENGLISH), entry(CHINESE.name(), CHINESE));

    public static BookCategory2 parseByValue(String value) {
        return valueMap.get(argNotBlank(value, "value"));
    }

    public static BookCategory2 parseByName(String name) {
        return nameMap.get(argNotBlank(name, "name"));
    }
}
