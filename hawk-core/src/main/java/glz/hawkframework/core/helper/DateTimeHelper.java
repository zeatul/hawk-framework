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
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * Thread-safe date-time utility methods.
 * <p>Uses {@link DateTimeFormatter} instead of {@link java.text.SimpleDateFormat} to avoid concurrency issues.</p>
 *
 * @author Zhang Peng
 */
public abstract class DateTimeHelper {

    /**
     * Compact date-time pattern with milliseconds and no delimiter, e.g. {@code 20251014143045123}.
     * <p>JDK 8's {@link DateTimeFormatter} cannot parse this pattern unambiguously (it treats it as
     * two-digit second plus variable-length fraction), so {@link #toLocaleDateTime(String, String)}
     * inserts a {@code "."} delimiter before the milliseconds and parses with
     * {@link #COMPACT_MILLISECOND_FORMATTER} instead.</p>
     */
    private static final String COMPACT_MILLISECOND_PATTERN = "yyyyMMddHHmmssSSS";
    private static final DateTimeFormatter COMPACT_MILLISECOND_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");

    // Thread-safe cache for DateTimeFormatter instances.
    private static final ConcurrentMap<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

    // Thread-safe cache for zone-aware DateTimeFormatter instances (used by java.util.Date formatting).
    private static final ConcurrentMap<String, DateTimeFormatter> ZONED_FORMATTER_CACHE = new ConcurrentHashMap<>();

    /**
     * Parses a string into a {@link LocalDate}.
     *
     * @param dateStr       the date string to parse, must not be {@code null}
     * @param formatPattern the pattern used for parsing, must not be {@code null}
     * @return the parsed {@link LocalDate}
     */
    public static LocalDate toLocaleDate(@Nonnull String dateStr, @Nonnull String formatPattern) {
        return LocalDate.parse(argNotNull(dateStr, "dateStr"), getFormatter(argNotNull(formatPattern, "formatPattern")));
    }

    /**
     * Parses a string into a {@link LocalTime}.
     *
     * @param timeStr       the time string to parse, must not be {@code null}
     * @param formatPattern the pattern used for parsing, must not be {@code null}
     * @return the parsed {@link LocalTime}
     */
    public static LocalTime toLocaleTime(@Nonnull String timeStr, @Nonnull String formatPattern) {
        return LocalTime.parse(argNotNull(timeStr, "timeStr"), getFormatter(argNotNull(formatPattern, "formatPattern")));
    }

    /**
     * Parses a string into a {@link LocalDateTime}.
     * <p>When {@code formatPattern} equals {@link #COMPACT_MILLISECOND_PATTERN}, a {@code "."} delimiter
     * is inserted before the milliseconds so that the value can be parsed unambiguously.</p>
     *
     * @param dateTimeStr   the date-time string to parse, must not be {@code null}
     * @param formatPattern the pattern used for parsing, must not be {@code null}
     * @return the parsed {@link LocalDateTime}
     */
    public static LocalDateTime toLocaleDateTime(@Nonnull String dateTimeStr, @Nonnull String formatPattern) {
        argNotNull(dateTimeStr, "dateTimeStr");
        argNotNull(formatPattern, "formatPattern");
        if (COMPACT_MILLISECOND_PATTERN.equals(formatPattern)) {
            return LocalDateTime.parse(new StringBuilder(dateTimeStr).insert(dateTimeStr.length() - 3, ".").toString(), COMPACT_MILLISECOND_FORMATTER);
        }
        return LocalDateTime.parse(dateTimeStr, getFormatter(formatPattern));
    }

    /**
     * Formats a {@link LocalDate} into a string.
     *
     * @param localDate     the value to format, must not be {@code null}
     * @param formatPattern the pattern used for formatting, must not be {@code null}
     * @return the formatted string
     */
    public static String format(@Nonnull LocalDate localDate, @Nonnull String formatPattern) {
        return argNotNull(localDate, "localDate").format(getFormatter(argNotNull(formatPattern, "formatPattern")));
    }

    /**
     * Formats a {@link LocalDateTime} into a string.
     *
     * @param localDateTime the value to format, must not be {@code null}
     * @param formatPattern the pattern used for formatting, must not be {@code null}
     * @return the formatted string
     */
    public static String format(@Nonnull LocalDateTime localDateTime, @Nonnull String formatPattern) {
        return argNotNull(localDateTime, "localDateTime").format(getFormatter(argNotNull(formatPattern, "formatPattern")));
    }

    /**
     * Formats a {@link LocalTime} into a string.
     *
     * @param localTime     the value to format, must not be {@code null}
     * @param formatPattern the pattern used for formatting, must not be {@code null}
     * @return the formatted string
     */
    public static String format(@Nonnull LocalTime localTime, @Nonnull String formatPattern) {
        return argNotNull(localTime, "localTime").format(getFormatter(argNotNull(formatPattern, "formatPattern")));
    }

    /**
     * Formats a {@link Date} into a string in a thread-safe manner.
     * <p>The {@link Date} is converted to an {@link Instant} and formatted via a
     * {@link DateTimeFormatter}, avoiding the concurrency issues of {@link java.text.SimpleDateFormat}.</p>
     *
     * @param date          the value to format, must not be {@code null}
     * @param formatPattern the pattern used for formatting, must not be {@code null}
     * @return the formatted string
     */
    public static String format(@Nonnull Date date, @Nonnull String formatPattern) {
        argNotNull(date, "date");
        argNotNull(formatPattern, "formatPattern");
        Instant instant = date.toInstant();
        // Use the system default zone to keep the same behaviour as the former SimpleDateFormat usage.
        DateTimeFormatter formatter = ZONED_FORMATTER_CACHE.computeIfAbsent(formatPattern,
            p -> DateTimeFormatter.ofPattern(p).withZone(ZoneId.systemDefault()));
        return formatter.format(instant);
    }

    /**
     * Returns a cached {@link DateTimeFormatter} for the given pattern, creating it if absent.
     * <p>{@link DateTimeFormatter} is immutable and therefore safe to share across threads.</p>
     *
     * @param pattern the pattern, must not be {@code null}
     * @return the cached formatter
     */
    @Nonnull
    private static DateTimeFormatter getFormatter(@Nonnull String pattern) {
        return FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
    }

    /**
     * Clears the internal formatter caches.
     * <p>Intended for tests or explicit memory release.</p>
     */
    public static void clearFormatterCache() {
        FORMATTER_CACHE.clear();
        ZONED_FORMATTER_CACHE.clear();
    }

}
