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

package glz.hawkframework.dao.column;

import glz.hawkframework.dao.column.annotation.*;
import glz.hawkframework.dao.column.support.BookPo;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class PojoFieldParserTest {

    @Test
    public void testParseAndModifyFields() {
        PojoFieldParser<BookPo> pojoFieldParser = new PojoFieldParser<>(BookPo.class);
        Map<Class<? extends Annotation>, List<FieldDescriptor<BookPo, Object, Annotation>>> map = pojoFieldParser.parse();
        
        // Verify the number of parsed fields (BookPo should contain id, createTime, updateTime, createUser, updateUser, version, etc.)
        assertThat(map).isNotEmpty();
        
        BookPo bookPo = new BookPo();
        bookPo.setCreateTime(LocalDateTime.now());
        bookPo.setUpdateTime(LocalDateTime.now());
        bookPo.setId("111111");
        bookPo.setCreateUser("A1011");
        bookPo.setUpdateUser("A1012");

        // Test modification of the RecordId field
        FieldDescriptor<BookPo, Object, Annotation> fieldDescriptor = map.get(RecordId.class).get(0);
        assertThat(fieldDescriptor).isNotNull();
        fieldDescriptor.getFieldModifier().accept(bookPo, "XX10001");
        assertThat(bookPo.getId()).isEqualTo("XX10001");

        // Test modification of the RecordCreateUserId field
        fieldDescriptor = map.get(RecordCreateUserId.class).get(0);
        assertThat(fieldDescriptor).isNotNull();
        fieldDescriptor.getFieldModifier().accept(bookPo, "XX10002");
        assertThat(bookPo.getCreateUser()).isEqualTo("XX10002");

        // Test modification of the RecordUpdateUserId field
        fieldDescriptor = map.get(RecordUpdateUserId.class).get(0);
        assertThat(fieldDescriptor).isNotNull();
        fieldDescriptor.getFieldModifier().accept(bookPo, "XX10003");
        assertThat(bookPo.getUpdateUser()).isEqualTo("XX10003");

        // Test modification of the RecordCreateDateTime field
        LocalDateTime newCreateTime = LocalDateTime.of(2024, 1, 1, 12, 0);
        fieldDescriptor = map.get(RecordCreateDateTime.class).get(0);
        assertThat(fieldDescriptor).isNotNull();
        fieldDescriptor.getFieldModifier().accept(bookPo, newCreateTime);
        assertThat(bookPo.getCreateTime()).isEqualTo(newCreateTime);

        // Test modification of the RecordUpdateDateTime field
        LocalDateTime newUpdateTime = LocalDateTime.of(2024, 1, 2, 12, 0);
        fieldDescriptor = map.get(RecordUpdateDateTime.class).get(0);
        assertThat(fieldDescriptor).isNotNull();
        fieldDescriptor.getFieldModifier().accept(bookPo, newUpdateTime);
        assertThat(bookPo.getUpdateTime()).isEqualTo(newUpdateTime);

        // Verify that the cache takes effect: parsing again should return the same result
        Map<Class<? extends Annotation>, List<FieldDescriptor<BookPo, Object, Annotation>>> map2 = pojoFieldParser.parse();
        assertThat(map2).isSameAs(map);
    }
}
