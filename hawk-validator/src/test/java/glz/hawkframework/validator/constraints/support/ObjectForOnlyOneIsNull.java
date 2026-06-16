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

import glz.hawkframework.validator.constraints.NotNullGroup;

import static glz.hawkframework.validator.constraints.NotNullGroup.NotNullType.ONLY_ONE_IS_NOT_NULL;
import static glz.hawkframework.validator.constraints.NotNullGroup.NotNullType.ONLY_ONE_IS_NULL;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
@NotNullGroup(fields = {"a", "b", "c", "d"}, type = ONLY_ONE_IS_NULL)
public class ObjectForOnlyOneIsNull {

    private String a;
    private String b;
    private String c;
    private String d;

    public String getA() {
        return this.a;
    }

    public void setA(String a) {
        this.a = a;
    }

    public String getB() {
        return this.b;
    }

    public void setB(String b) {
        this.b = b;
    }

    public String getC() {
        return this.c;
    }

    public void setC(String c) {
        this.c = c;
    }

    public String getD() {
        return this.d;
    }

    public void setD(String d) {
        this.d = d;
    }
}
