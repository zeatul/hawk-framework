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

package glz.hawkframework.core.pojo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class PojoClassParser {

    private final HashSet<Class<?>> BASIC_TYPES = new HashSet<>();

    {
        // Java基本类型及其包装类
        BASIC_TYPES.add(boolean.class);
        BASIC_TYPES.add(Boolean.class);
        BASIC_TYPES.add(byte.class);
        BASIC_TYPES.add(Byte.class);
        BASIC_TYPES.add(char.class);
        BASIC_TYPES.add(Character.class);
        BASIC_TYPES.add(short.class);
        BASIC_TYPES.add(Short.class);
        BASIC_TYPES.add(int.class);
        BASIC_TYPES.add(Integer.class);
        BASIC_TYPES.add(long.class);
        BASIC_TYPES.add(Long.class);
        BASIC_TYPES.add(float.class);
        BASIC_TYPES.add(Float.class);
        BASIC_TYPES.add(double.class);
        BASIC_TYPES.add(Double.class);
        BASIC_TYPES.add(void.class);
        BASIC_TYPES.add(Void.class);

        // String和常见不可递归类型
        BASIC_TYPES.add(String.class);
        BASIC_TYPES.add(BigDecimal.class);
        BASIC_TYPES.add(BigInteger.class);

        // 日期时间类型
        BASIC_TYPES.add(Date.class);
        BASIC_TYPES.add(java.sql.Date.class);
        BASIC_TYPES.add(LocalDate.class);
        BASIC_TYPES.add(LocalTime.class);
        BASIC_TYPES.add(LocalDateTime.class);

        // 枚举类型（通常不递归）
        BASIC_TYPES.add(Enum.class);
    }


    public PojoClassParser() {

    }

    public PojoClassParser(List<Class<?>> extraBasicTypes) {
        if (extraBasicTypes != null) {
            BASIC_TYPES.addAll(extraBasicTypes);
        }
    }

    public boolean isSimple(Class<?> clazz) {
        return BASIC_TYPES.contains(clazz) || clazz.isEnum();
    }

    public boolean isCollectionOrArray(Class<?> clazz) {
        return clazz.isArray() || Iterable.class.isAssignableFrom(clazz);
    }

}
