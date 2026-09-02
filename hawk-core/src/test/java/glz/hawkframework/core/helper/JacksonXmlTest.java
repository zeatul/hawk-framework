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

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.deser.FromXmlParser;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class JacksonXmlTest {

    private final XmlMapper xmlMapper;

    public JacksonXmlTest(){
        xmlMapper = new XmlMapper();
        xmlMapper.configure(FromXmlParser.Feature.EMPTY_ELEMENT_AS_NULL, true);
        xmlMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new EmptyStringToNullStringDeserializer());
        xmlMapper.registerModule(module);
    }

    @Test
    public void empty_string_is_null_test() throws JsonProcessingException {
//        String xml = "<book><title></title></book>";
        String xml = "<book><title/></book>";
        Book book = xmlMapper.readValue(xml, Book.class);
        assertThat(book).isNotNull();
        assertThat(book.getTitle()).isNull();
    }

    public static class EmptyStringToNullStringDeserializer extends JsonDeserializer<String>{

        @Override
        public String deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            String value = jsonParser.getValueAsString();
            return (value == null || value.isEmpty()) ? null : value;
        }
    }

    public static class Book {
        private String title;

        public String getTitle() {
            return this.title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
