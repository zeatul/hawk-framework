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

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class ReflectHelperTest {

    @Test
    public void test() {
        System.out.println("ReflectHelperTest");

        People p = ReflectHelper.instance(People.class, 1100, "Jackson", "Worker");
        assertThat(p.id).isEqualTo(1100);

        p = ReflectHelper.instance(People.class, new Class<?>[]{Integer.class, String.class, String.class}, new Object[]{100, "Jackson", "Worker"});
        assertThat(p.id).isEqualTo(100);

        p = ReflectHelper.instance(People.class, new Class<?>[]{Integer.class, String.class, String.class, List.class}, new Object[]{300, "Jackson", "Worker", new ArrayList<>()});
        assertThat(p.id).isEqualTo(300);
    }

    @Test
    public void test_no_argument() {
        People p = ReflectHelper.instance(People.class);
        assertThat(p.id).isNull();
    }


    public static class People {
        public final Integer id;
        public final String name;
        public final String description;
        public final List<People> friendNames;

        public People() {
            this.id = null;
            this.name = null;
            this.description = null;
            this.friendNames = null;
        }

        public People(Integer id, String name, String description, List<People> friendNames) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.friendNames = friendNames;
        }

        public People(Integer id, String name, String description) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.friendNames = new ArrayList<>();
        }
    }
}
