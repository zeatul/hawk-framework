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

import glz.hawkframework.validator.constraints.UniversalCheck;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
@UniversalCheck(checkerName = "LESS")
public class ObjectForUniversalCheck {

    private Integer age1;
    private Integer age2;

    public Integer getAge1() {
        return this.age1;
    }

    public void setAge1(Integer age1) {
        this.age1 = age1;
    }

    public Integer getAge2() {
        return this.age2;
    }

    public void setAge2(Integer age2) {
        this.age2 = age2;
    }
}
