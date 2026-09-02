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

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class PojoFieldParserTest {

    @Test
    public void test() {
        PojoFieldParser<BookPo> pojoFieldParser = new PojoFieldParser<>(BookPo.class);
        Map<Class<? extends Annotation>, List<FieldDescriptor<BookPo, Object, Annotation>>> map = pojoFieldParser.parse();
        BookPo bookPo = new BookPo();
        bookPo.setCreateTime(LocalDateTime.now());
        bookPo.setUpdateTime(LocalDateTime.now());
        bookPo.setId("111111");
        bookPo.setCreateUser("A1011");
        bookPo.setUpdateUser("A1012");
        System.out.println(map.size());
        System.out.println("-----------------------------------------");

        FieldDescriptor<BookPo, Object,Annotation> fieldDescriptor = map.get(RecordId.class).get(0);
        if (fieldDescriptor != null) {
            fieldDescriptor.getFieldModifier().accept(bookPo, "XX10001");
        }

        fieldDescriptor = map.get(RecordCreateUserId.class).get(0);
        if (fieldDescriptor != null) {
            fieldDescriptor.getFieldModifier().accept(bookPo, "XX10002");
        }

        fieldDescriptor = map.get(RecordUpdateUserId.class).get(0);
        if (fieldDescriptor != null) {
            fieldDescriptor.getFieldModifier().accept(bookPo, "XX10003");
        }

        fieldDescriptor = map.get(RecordCreateDateTime.class).get(0);
        if (fieldDescriptor != null) {
            fieldDescriptor.getFieldModifier().accept(bookPo, LocalDateTime.now());
        }

        fieldDescriptor = map.get(RecordUpdateDateTime.class).get(0);
        if (fieldDescriptor != null) {
            fieldDescriptor.getFieldModifier().accept(bookPo, LocalDateTime.now());
        }

        map.values().forEach(descriptors -> {
            System.out.printf("updated %s = %s%n", descriptors.get(0).getFieldName(), descriptors.get(0).getFieldSupplier().apply(bookPo));
        });
    }
}
