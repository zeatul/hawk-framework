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

package glz.hawkframework.core.tuple;

import java.util.Objects;

/**
 * This class is responsible for
 *
 * @author Zhang Peng
 */
public class Tuple3<T1, T2, T3> {
    public final T1 t1;
    public final T2 t2;
    public final T3 t3;

    private Tuple3(T1 t1, T2 t2, T3 t3) {
        this.t1 = t1;
        this.t2 = t2;
        this.t3 = t3;
    }

    public static <T1, T2, T3> Tuple3<T1, T2, T3> of(T1 t1, T2 t2, T3 t3) {
        return new Tuple3<>(t1, t2, t3);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tuple3)) return false;
        Tuple3<?, ?, ?> that = (Tuple3<?, ?, ?>) o;
        return Objects.equals(t1, that.t1) && Objects.equals(t2, that.t2) && Objects.equals(t3, that.t3);
    }

    @Override
    public int hashCode() {
        return Objects.hash(t1, t2, t3);
    }

    @Override
    public String toString() {
        return "Tuple3{t1=" + t1 + ", t2=" + t2 + ", t3=" + t3 + '}';
    }
}
