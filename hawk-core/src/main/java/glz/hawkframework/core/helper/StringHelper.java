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


import javax.annotation.Nullable;
import java.util.Arrays;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;
import static glz.hawkframework.core.support.ArgumentSupport.argument;

/**
 * Miscellaneous {@link String} utility methods.
 * <p>Mainly for internal use within the framework.</p>
 *
 * @author Hawk
 */
public abstract class StringHelper {

    /**
     * Check that the given {@code CharSequence} is {@code null} or of length 0.
     *
     * <pre class="code">
     * StringHelper.isEmpty(null) = true
     * StringHelper.isEmpty("") = true
     * StringHelper.isEmpty(" ") = false
     * StringHelper.isEmpty("12345") = false
     * StringHelper.isEmpty(" 12345 ") = false
     * </pre>
     *
     * @param str the {@code CharSequence} to check
     * @return {@code true} if the {@code CharSequence} is {@code null} or of length 0; {@code false} otherwise
     */
    public static boolean isEmpty(@Nullable CharSequence str) {
        return (str == null || str.length() == 0);
    }

    /**
     * Check that the given {@code CharSequence} is neither {@code null} nor of length 0.
     *
     * @param str the {@code CharSequence} to check
     * @return {@code true} if the {@code CharSequence} is neither {@code null} nor of length 0.
     */
    public static boolean isNotEmpty(@Nullable CharSequence str) {
        return (str != null && str.length() > 0);
    }

    /**
     * Check that the given {@code String} contains no actual <em>text</em>.
     * <p>
     * More specifically, this method returns {@code true} if the {@code String} is {@code null},
     * its length is 0, or it only contains whitespace character.
     * </p>
     *
     * <pre class="code">
     * StringUtils.isBlank(null) = true
     * StringUtils.isBlank("") = true
     * StringUtils.isBlank(" ") = true
     * StringUtils.isBlank("12345") = false
     * StringUtils.isBlank(" 12345 ") = false
     * </pre>
     *
     * @param str the {@code String} to check
     * @return {@code true} if the {@code String} is blank; {@code false} otherwise
     * @see Character#isWhitespace
     */
    public static boolean isBlank(@Nullable String str) {
        return (str == null || str.trim().isEmpty());
    }

    /**
     * Check that the given {@code String} contains actual <em>text</em>.
     * <p>
     * More specifically, this method returns {@code true} if the {@code String} is not {@code null},
     * its length is greater than 0, and it contains at least one non-whitespace character.
     *
     * @param str the {@code String} to check
     * @return {@code true} if the {@code String} is not blank; {@code false} otherwise
     * @see #isBlank(String)
     */
    public static boolean isNotBlank(@Nullable String str) {
        return !isBlank(str);
    }

    /**
     * Capitalize a {@code String}, changing the first letter to upper case as per {@link Character#toUpperCase(char)}.
     * No other letters are changed.
     *
     * @param str the {@code String} to capitalize
     * @return the capitalized {@code String}
     */
    public static String capitalize(@Nullable String str) {
        return changeFirstCharacterCase(str, true);
    }

    /**
     * UnCapitalize a {@code String}, changing the first letter to lower case as per {@link Character#toLowerCase(char)}.
     * No other letters are changed.
     *
     * @param str the {@code String} to uncapitalize
     * @return the unCapitalize {@code String}
     */
    public static String unCapitalize(@Nullable String str) {
        return changeFirstCharacterCase(str, false);
    }

    private static String changeFirstCharacterCase(String str, boolean capitalize) {
        if (isEmpty(str)) {
            return str;
        }
        char baseChar = str.charAt(0);
        char updatedChar;
        if (capitalize) {
            updatedChar = Character.toUpperCase(baseChar);
        } else {
            updatedChar = Character.toLowerCase(baseChar);
        }
        if (baseChar == updatedChar) {
            return str;
        }
        char[] chars = str.toCharArray();
        chars[0] = updatedChar;
        return new String(chars);
    }


    /**
     * Replaces the contents of the string at the specified location
     *
     * @param inString   The replaced string
     * @param start      Start index, inclusive
     * @param end        End index，exclusive
     * @param newPattern {@code String} to replace the substring in the given index.
     * @return a {@code String} with the replacements
     */

    public static String replace(String inString, int start, int end, String newPattern) {
        if (isEmpty(inString)) {
            return inString;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(inString);
        sb.replace(start, end, newPattern);
        return sb.toString();
    }

    /**
     * Replace all occurrences of a substring within a string with another string.
     *
     * @param inString   {@code String} to examine
     * @param oldPattern {@code String} to replace
     * @param newPattern {@code String} to insert
     * @return a {@code String} with the replacements
     */
    public static String replace(String inString, String oldPattern, @Nullable String newPattern) {
        if (isEmpty(inString) || isEmpty(oldPattern) || newPattern == null) {
            return inString;
        }
        int index = inString.indexOf(oldPattern);
        if (index == -1) {
            // no occurrence -> can return input as-is
            return inString;
        }

        int capacity = inString.length();
        if (newPattern.length() > oldPattern.length()) {
            capacity += 16;
        }
        StringBuilder sb = new StringBuilder(capacity);

        int pos = 0;  // our position in the old string
        int patLen = oldPattern.length();
        while (index >= 0) {
            sb.append(inString, pos, index);
            sb.append(newPattern);
            pos = index + patLen;
            index = inString.indexOf(oldPattern, pos);
        }

        // append any characters to the right of a match
        sb.append(inString, pos, inString.length());
        return sb.toString();
    }

    /**
     * Obtains a {@code String} with c repeated 'n' times.
     * <p>If n is 0, return {@code ""}.</p>
     *
     * @param c the char to be repeated.
     * @param n the repeat times.
     * @return a {@code String}.
     */
    public static String repeatChar(char c, int n) {
        argNotNull(c, "c");

        if (n > 0) {
            char[] arr = new char[n];
            Arrays.fill(arr, c);
            return new String(arr);
        }

        if (n == 0) {
            return "";
        }

        throw new IllegalArgumentException("The parameter: 'n' can't be less than 0.");
    }

    /**
     * Obtains a {@code String} with str repeated 'n' times.
     * <p>If n is 0, return {@code ""}.</p>
     *
     * @param str the char to be repeated.
     * @param n   the repeat times.
     * @return a {@code String}.
     */
    public static String repeatStr(String str, int n) {
        argNotNull(str, "str");
        argument(n, i -> i > 0, i -> "The parameter: 'n' can't be less than 0.");
        StringBuilder sb = new StringBuilder();
        if (n > 0) {
            for (int i = 0; i < n; i++) {
                sb.append(str);
            }
            return sb.toString();
        } else if (n == 0) {
            return "";
        } else {
            throw new IllegalArgumentException("The parameter: 'n' can't be less than 0.");
        }
    }
}
