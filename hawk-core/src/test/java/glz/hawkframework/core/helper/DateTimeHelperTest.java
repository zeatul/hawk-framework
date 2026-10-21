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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class DateTimeHelperTest {

    @Test
    public void toLocaleDate_test() {
        LocalDate date = DateTimeHelper.toLocaleDate("2025-10-14", "yyyy-MM-dd");
        assertThat(date).isEqualTo(LocalDate.of(2025, 10, 14));

        date = DateTimeHelper.toLocaleDate("20251014", "yyyyMMdd");
        assertThat(date).isEqualTo(LocalDate.of(2025, 10, 14));
    }

    @Test
    public void toLocaleTime_test() {
        LocalTime time = DateTimeHelper.toLocaleTime("14:30:45", "HH:mm:ss");
        assertThat(time).isEqualTo(LocalTime.of(14, 30, 45));

        time = DateTimeHelper.toLocaleTime("143045", "HHmmss");
        assertThat(time).isEqualTo(LocalTime.of(14, 30, 45));
    }

    @Test
    public void toLocaleDateTime_test() {
        LocalDateTime dt = DateTimeHelper.toLocaleDateTime("2025-10-14 14:30:45", "yyyy-MM-dd HH:mm:ss");
        assertThat(dt).isEqualTo(LocalDateTime.of(2025, 10, 14, 14, 30, 45));

        dt = DateTimeHelper.toLocaleDateTime("20251014143045", "yyyyMMddHHmmss");
        assertThat(dt).isEqualTo(LocalDateTime.of(2025, 10, 14, 14, 30, 45));
    }

    @Test
    public void toLocaleDateTime_compactMillisecondPattern_test() {
        // JDK 8 parses the delimiter-free millisecond pattern (yyyyMMddHHmmssSSS) ambiguously:
        // DateTimeFormatter treats it as a two-digit second plus a variable-length fraction,
        // producing an incorrect result. DateTimeHelper works around this by inserting a "."
        // delimiter before parsing. NOTE: this workaround targets that specific pattern; do not remove it.
        LocalDateTime dt = DateTimeHelper.toLocaleDateTime("20251014143045123", "yyyyMMddHHmmssSSS");
        assertThat(dt).isEqualTo(LocalDateTime.of(2025, 10, 14, 14, 30, 45, 123_000_000));
    }

    @Test
    public void format_test() {
        String formatted = DateTimeHelper.format(LocalDate.of(2025, 10, 14), "yyyy-MM-dd");
        assertThat(formatted).isEqualTo("2025-10-14");

        formatted = DateTimeHelper.format(LocalDateTime.of(2025, 10, 14, 14, 30, 45), "yyyy-MM-dd HH:mm:ss");
        assertThat(formatted).isEqualTo("2025-10-14 14:30:45");

        formatted = DateTimeHelper.format(LocalTime.of(14, 30, 45), "HH:mm:ss");
        assertThat(formatted).isEqualTo("14:30:45");
    }

    @Test
    public void invalidPattern_test() {
        assertThatThrownBy(() -> DateTimeHelper.toLocaleDate("2025-10-14", "invalid"))
            .isInstanceOf(Exception.class);

        assertThatThrownBy(() -> DateTimeHelper.toLocaleDateTime("20251014143045", "invalid"))
            .isInstanceOf(Exception.class);
    }
}