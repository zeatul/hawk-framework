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

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class LocaleHelperTest {

    @Test
    public void format_test() {
        // Standard BCP 47 tags
        assertThat(LocaleHelper.format("zh-CN")).isEqualTo("zh-CN");
        assertThat(LocaleHelper.format("zh-Hans")).isEqualTo("zh-Hans");
        assertThat(LocaleHelper.format("en-US")).isEqualTo("en-US");
        assertThat(LocaleHelper.format("en-GB")).isEqualTo("en-GB");
        assertThat(LocaleHelper.format("ja-JP")).isEqualTo("ja-JP");
        
        // Underscore variant (converted to hyphen)
        assertThat(LocaleHelper.format("zh_CN")).isEqualTo("zh-CN");
        assertThat(LocaleHelper.format("en_US")).isEqualTo("en-US");
        
        // 3-letter language codes (ISO 639-2/T)
        assertThat(LocaleHelper.format("zho")).isEqualTo("zho");
        assertThat(LocaleHelper.format("eng")).isEqualTo("eng");
        
        // Legacy 2-char format
        assertThat(LocaleHelper.format("zh")).isEqualTo("zh");
        assertThat(LocaleHelper.format("en")).isEqualTo("en");
        
        // Legacy 5-char format
        assertThat(LocaleHelper.format("zh_CN")).isEqualTo("zh-CN");
        assertThat(LocaleHelper.format("zh-CN")).isEqualTo("zh-CN");
        
        // Invalid formats
        assertThatThrownBy(() -> LocaleHelper.format(""))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocaleHelper.format("zh1"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocaleHelper.format("zh-C"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocaleHelper.format("zh_C"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void parse_test() {
        // Standard BCP 47 tags
        assertThat(LocaleHelper.parse("zh-CN")).isEqualTo(Locale.SIMPLIFIED_CHINESE);
        assertThat(LocaleHelper.parse("zh-Hans")).isEqualTo(Locale.forLanguageTag("zh-Hans"));
        assertThat(LocaleHelper.parse("en-US")).isEqualTo(Locale.US);
        assertThat(LocaleHelper.parse("en-GB")).isEqualTo(Locale.UK);
        
        // Underscore variant
        assertThat(LocaleHelper.parse("zh_CN")).isEqualTo(Locale.SIMPLIFIED_CHINESE);
        
        // 3-letter language codes (returns language-only Locale)
        Locale zho = LocaleHelper.parse("zho");
        assertThat(zho.getLanguage()).isEqualTo("zho");
        assertThat(zho.getCountry()).isEmpty();
        
        Locale eng = LocaleHelper.parse("eng");
        assertThat(eng.getLanguage()).isEqualTo("eng");
        assertThat(eng.getCountry()).isEmpty();
        
        // Legacy 2-char format
        assertThat(LocaleHelper.parse("zh")).isEqualTo(Locale.forLanguageTag("zh"));
        assertThat(LocaleHelper.parse("en")).isEqualTo(Locale.forLanguageTag("en"));
        
        // Legacy 5-char format
        assertThat(LocaleHelper.parse("zh-CN")).isEqualTo(Locale.SIMPLIFIED_CHINESE);
        assertThat(LocaleHelper.parse("zh_CN")).isEqualTo(Locale.SIMPLIFIED_CHINESE);
        
        // Invalid formats
        assertThatThrownBy(() -> LocaleHelper.parse(""))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocaleHelper.parse("zh1"))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocaleHelper.parse("zh-C"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void getLanguage_test() {
        // Standard BCP 47 tags
        assertThat(LocaleHelper.getLanguage("zh-CN")).isEqualTo("zh");
        assertThat(LocaleHelper.getLanguage("zh-Hans")).isEqualTo("zh");
        assertThat(LocaleHelper.getLanguage("en-US")).isEqualTo("en");
        assertThat(LocaleHelper.getLanguage("ja-JP")).isEqualTo("ja");
        
        // Underscore variant
        assertThat(LocaleHelper.getLanguage("zh_CN")).isEqualTo("zh");
        assertThat(LocaleHelper.getLanguage("en_US")).isEqualTo("en");
        
        // 3-letter language codes
        assertThat(LocaleHelper.getLanguage("zho")).isEqualTo("zho");
        assertThat(LocaleHelper.getLanguage("eng")).isEqualTo("eng");
        
        // Legacy 2-char format
        assertThat(LocaleHelper.getLanguage("zh")).isEqualTo("zh");
        assertThat(LocaleHelper.getLanguage("en")).isEqualTo("en");
        
        // Legacy 5-char format
        assertThat(LocaleHelper.getLanguage("zh-CN")).isEqualTo("zh");
        assertThat(LocaleHelper.getLanguage("zh_CN")).isEqualTo("zh");
        
        // Extended tags (script, region, variants)
        assertThat(LocaleHelper.getLanguage("zh-Hans-CN")).isEqualTo("zh");
        assertThat(LocaleHelper.getLanguage("en-Latn-US")).isEqualTo("en");
        
        // Invalid formats
        assertThatThrownBy(() -> LocaleHelper.getLanguage(""))
            .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> LocaleHelper.getLanguage("zh1"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void get_locale_without_country_test() {
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        assertThat(LocaleHelper.getLanguage(locale.toString())).isEqualTo("zh");
        
        Locale noCountry = LocaleHelper.getLocaleWithoutCountry(Locale.SIMPLIFIED_CHINESE);
        assertThat(noCountry.getLanguage()).isEqualTo("zh");
        assertThat(noCountry.getCountry()).isEmpty();
        
        noCountry = LocaleHelper.getLocaleWithoutCountry(Locale.US);
        assertThat(noCountry.getLanguage()).isEqualTo("en");
        assertThat(noCountry.getCountry()).isEmpty();
    }
}
