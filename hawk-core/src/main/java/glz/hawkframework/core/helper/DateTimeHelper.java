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

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public abstract class DateTimeHelper {

    private static final String BUG_FORMAT_PATTERN = "yyyyMMddHHmmssSSS";
    private static final DateTimeFormatter BUG_FIXED_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");

    public static LocalDate toLocaleDate(@Nonnull String dateStr, @Nonnull String formatPattern) {
        return LocalDate.parse(argNotNull(dateStr, "dateStr"), DateTimeFormatter.ofPattern(argNotNull(formatPattern, "formatPattern")));
    }

    public static LocalTime toLocaleTime(@Nonnull String timeStr, @Nonnull String formatPattern) {
        return LocalTime.parse(argNotNull(timeStr, "timeStr"), DateTimeFormatter.ofPattern(argNotNull(formatPattern, "formatPattern")));
    }

    public static LocalDateTime toLocaleDateTime(@Nonnull String dateTimeStr, @Nonnull String formatPattern) {
        return BUG_FORMAT_PATTERN.equals(argNotNull(formatPattern, "formatPattern")) ? LocalDateTime.parse(new StringBuilder(dateTimeStr).insert(dateTimeStr.length() - 3, ".").toString(), BUG_FIXED_FORMATTER) : LocalDateTime.parse(argNotNull(dateTimeStr, "dateTimeStr"), DateTimeFormatter.ofPattern(formatPattern));
    }

    public static String format(@Nonnull LocalDate localDate, @Nonnull String formatPattern) {
        return argNotNull(localDate, "localDate").format(DateTimeFormatter.ofPattern(argNotNull(formatPattern, "formatPattern")));
    }

    public static String format(@Nonnull LocalDateTime localDateTime, @Nonnull String formatPattern) {
        return argNotNull(localDateTime, "localDateTime").format(DateTimeFormatter.ofPattern(argNotNull(formatPattern, "formatPattern")));
    }

    public static String format(@Nonnull LocalTime localTime, @Nonnull String formatPattern) {
        return argNotNull(localTime, "localTime").format(DateTimeFormatter.ofPattern(argNotNull(formatPattern, "formatPattern")));
    }

    public static String format(@Nonnull java.util.Date date, @Nonnull String formatPattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(argNotNull(formatPattern, "formatPattern"));
        return sdf.format(argNotNull(date, "date"));
    }


}
