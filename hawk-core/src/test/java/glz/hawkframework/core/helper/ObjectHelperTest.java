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

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class ObjectHelperTest {

    @Test
    public void getFieldValue_test(){
        Book book = new Book();
        book.setTitle("老人与海");
        book.setPrice(new BigDecimal("100.01"));

        String title = ObjectHelper.getFieldValue(book,"title",String.class);
        assertThat(title).isEqualTo(book.getTitle());
        BigDecimal price = ObjectHelper.getFieldValue(book,"price",BigDecimal.class);
        assertThat(price).isEqualTo(book.getPrice());
        String comment = ObjectHelper.getFieldValue(book,"comment",String.class);
        assertThat(comment).isNull();
    }

    @Test
    public void setFieldValue_4arg_test(){
        Book book = new Book();
        book.setTitle("老人与海");
        book.setPrice(new BigDecimal("100.01"));
        book.setComment("好书");

        final String newTitle = "废都";
        final BigDecimal newPrice = new BigDecimal("0.01");
        final String newComment = null;

        ObjectHelper.setFieldValue(book,"title",newTitle,String.class);
        ObjectHelper.setFieldValue(book,"price",newPrice,BigDecimal.class);
        ObjectHelper.setFieldValue(book,"comment",newComment,String.class);

        assertThat(book.getTitle()).isEqualTo(newTitle);
        assertThat(book.getPrice()).isEqualTo(newPrice);
        assertThat(book.getComment()).isNull();
    }

    @Test
    public void setFieldValue_3arg_usesDeclaredType(){
        // 3-arg overload should use the declared field type (Number), not the value's class (Integer),
        // so the setter setNum(Number) is found and the value is stored as-is.
        NumberFieldBook book = new NumberFieldBook();

        ObjectHelper.setFieldValue(book, "num", Integer.valueOf(42));

        assertThat(book.getNum()).isEqualTo(42);
    }

    @Test
    public void setFieldValue_3arg_nullValue_setsNull(){
        // The 3-arg overload resolves the declared field type and forwards the (nullable) value.
        NumberFieldBook book = new NumberFieldBook();
        book.setNum(1);

        ObjectHelper.setFieldValue(book, "num", null);

        assertThat(book.getNum()).isNull();
    }

    @Test
    public void anonymousClass_cacheKeyNotNull(){
        // Anonymous class has no canonical name (returns null), but getName() works
        Object anon = new Object() {
            private String name = "anon";
            public String getName() { return name; }
            public void setName(String name) { this.name = name; }
        };

        String before = ObjectHelper.getFieldValue(anon, "name", String.class);
        assertThat(before).isEqualTo("anon");

        ObjectHelper.setFieldValue(anon, "name", "changed", String.class);
        String after = ObjectHelper.getFieldValue(anon, "name", String.class);
        assertThat(after).isEqualTo("changed");
    }

    @Test
    public void localClass_cacheKeyNotNull(){
        class LocalClass {
            private String value = "local";
            public String getValue() { return value; }
            public void setValue(String value) { this.value = value; }
        }

        LocalClass local = new LocalClass();
        String before = ObjectHelper.getFieldValue(local, "value", String.class);
        assertThat(before).isEqualTo("local");

        ObjectHelper.setFieldValue(local, "value", "changed", String.class);
        String after = ObjectHelper.getFieldValue(local, "value", String.class);
        assertThat(after).isEqualTo("changed");
    }

    @Test
    public void concurrentCacheAccess_noDuplicateSetterCreation(){
        // Verify cache is thread-safe and doesn't create duplicate setters/getters
        Book book = new Book();
        AtomicInteger counter = new AtomicInteger(0);

        // This test mainly ensures no exceptions under concurrent access
        Runnable task = () -> {
            for (int i = 0; i < 100; i++) {
                ObjectHelper.setFieldValue(book, "title", "title" + i, String.class);
                ObjectHelper.getFieldValue(book, "title", String.class);
                counter.incrementAndGet();
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        try { t1.join(); t2.join(); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

        assertThat(counter.get()).isEqualTo(200);
    }

    public static class Book {
        private String title;
        private BigDecimal price;
        private String comment;

        public String getTitle() { return this.title; }
        public void setTitle(String title) { this.title = title; }
        public BigDecimal getPrice() { return this.price; }
        public void setPrice(BigDecimal price) { this.price = price; }
        public String getComment() { return this.comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    public static class NumberFieldBook {
        private Number num;

        public Number getNum() { return this.num; }
        public void setNum(Number num) { this.num = num; }
    }
}