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

package glz.hawkframework.dao.context;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

/**
 * This interface is responsible for
 *
 * @author Zhang Peng
 */
public interface RepositoryContext {

    /**
     * found no record
     */
    String FOUND_NONE = "FOUND_NONE";
    /**
     * found more than one records
     */
    String FOUND_MULTIPLE = "FOUND_MULTIPLE";

    /**
     * The count of affected rows is unexpected.
     */
    String AFFECTED_ROWS_COUNT_IS_UNEXPECTED = "AFFECTED_ROWS_COUNT_IS_UNEXPECTED";

    @Nonnull
    Class<?> getRepositoryClass();

    @Nonnull
    String getRepositoryMethodName();

    @Nonnull
    List<Param> getRepositoryMethodParams();

    @Nonnull
    SqlMethod getSqlMethod();

    /**
     * The discriminator is used to identify the special logic in the method.
     */
    @Nullable
    String getDiscriminator();

    /**
     * poClass is used to find the corresponding table
     */
    @Nullable
    Class<?> getPoClass();

    /**
     * the exception threw in the repository method.
     */
    @Nullable
    Exception getInitCause();

}
