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
    public void format_test(){
        final String correct = "zh-CN";
        assertThat(LocaleHelper.format("zh-CN")).isEqualTo(correct);
        assertThat(LocaleHelper.format("zh_CN")).isEqualTo(correct);
        assertThatThrownBy(() -> LocaleHelper.format("zh1")).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void parse_test(){
        final Locale correct = Locale.SIMPLIFIED_CHINESE;
        assertThat(LocaleHelper.parse("zh-CN")).isEqualTo(correct);
        assertThat(LocaleHelper.parse("zh_CN")).isEqualTo(correct);
        assertThatThrownBy(() -> LocaleHelper.parse("zh1")).isInstanceOf(RuntimeException.class);
    }

    @Test
    public void get_locale_without_country_test(){
        final String correct = "zh";
        Locale locale = Locale.SIMPLIFIED_CHINESE;
        assertThat(LocaleHelper.getLanguage(locale.toString())).isEqualTo(correct);

    }
}
