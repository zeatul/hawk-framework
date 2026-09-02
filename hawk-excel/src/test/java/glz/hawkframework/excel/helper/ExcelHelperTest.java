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

package glz.hawkframework.excel.helper;

import glz.hawkframework.excel.helper.support.Author;
import glz.hawkframework.excel.helper.support.Book;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class ExcelHelperTest {

    @Test
    public void testWriteExcelWithTemplate(){
        File templateFile = new File("e:/temp/test_template.xlsx");
        File outputFile = new File("e:/temp/test_out.xlsx");
        Map<Class<?>,List<?>> excelData = createExcelData();
        ExcelHelper.writeExcelWithTemplate(templateFile,outputFile,excelData);
    }

    @Test
    public void testWriteExcel(){
        File outputFile = new File("e:/temp/test_out.xlsx");
        Map<Class<?>,List<?>> excelData = createExcelData();
        ExcelHelper.writeExcel(outputFile,excelData);
    }

    private Map<Class<?>,List<?>> createExcelData(){
        return ExcelHelper.readSheets("/glz/hawkframework/excel/helper/test.xlsx", Book.class, Author.class);
    }

    @Test
    public void testReadOneSheet() {
//        File file = new File("e:/temp/test.xlsx");
        List<Book> books = ExcelHelper.readOneSheet("/glz/hawkframework/excel/helper/test.xlsx", Book.class);
        assertThat(books).hasSize(2);

        Book book = books.get(0);
        assertThat(book).extracting(Book::getRowNo, Book::getName, Book::getPrice, Book::getPublishDate, Book::getValid, Book::getCount)
            .containsExactly(1, "西游记", new BigDecimal("10.99"), LocalDate.of(1975, 9, 9), false, 20);

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testReadSheets() {
//        File file = new File("e:/temp/test.xlsx");
        Map<Class<?>, List<?>> map = ExcelHelper.readSheets("/glz/hawkframework/excel/helper/test.xlsx", Book.class, Author.class);
        assertThat(map).hasSize(2);

        List<Book> books = (List<Book>) map.get(Book.class);
        assertThat(books).hasSize(2);
        Book book = books.get(0);
        assertThat(book).extracting(Book::getRowNo, Book::getName, Book::getPrice, Book::getPublishDate, Book::getValid, Book::getCount)
            .containsExactly(1, "西游记", new BigDecimal("10.99"), LocalDate.of(1975, 9, 9), false, 20);

        List<Author> authors = (List<Author>) map.get(Author.class);
        assertThat(authors).hasSize(2);
        Author author = authors.get(0);
        assertThat(author).extracting(Author::getAuthorName, Author::getAuthorAge)
            .containsExactly("张无忌", 111);
    }
}
