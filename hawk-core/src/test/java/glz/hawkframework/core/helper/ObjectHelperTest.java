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
        assertThat(comment).isNull();;
    }

    @Test
    public void setFieldValue_test(){
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

    public static class Book {
        private String title;
        private BigDecimal price;
        private String comment;

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public BigDecimal getPrice() {
            return this.price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }

        public String getComment() {
            return this.comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }
    }
}
