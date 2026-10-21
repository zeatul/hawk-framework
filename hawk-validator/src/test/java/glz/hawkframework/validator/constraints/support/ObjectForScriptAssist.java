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

package glz.hawkframework.validator.constraints.support;

import org.hibernate.validator.constraints.ParameterScriptAssert;
import org.hibernate.validator.constraints.ScriptAssert;

import java.time.LocalDate;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
@ScriptAssert(
    lang = "spring",  // Specify/use Spring EL
    script = "#root.startDate < #root.endDate", // This also works
//    script = "startDate < endDate", // This also works
    message = "开始日期必须在结束日期之前"
)
public class ObjectForScriptAssist {
    private LocalDate startDate;
    private LocalDate endDate;

    public LocalDate getStartDate() {
        return this.startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return this.endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
}
