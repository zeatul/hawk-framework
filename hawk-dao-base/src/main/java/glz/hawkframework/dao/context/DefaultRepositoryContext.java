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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static glz.hawkframework.core.support.ArgumentSupport.argNotBlank;
import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultRepositoryContext implements RepositoryContext {

    private final Class<?> repositoryClass;
    private final String repositoryMethodName;
    private final List<Param> repositoryMethodParams;
    private final SqlMethod sqlMethod;
    private final String discriminator;
    private final Class<?> poClass;
    private final Exception initCause;

    private DefaultRepositoryContext(Builder builder) {
        this.repositoryClass = builder.repositoryClass;
        this.repositoryMethodName = builder.repositoryMethodName;
        this.repositoryMethodParams = builder.repositoryMethodParams;
        this.sqlMethod = builder.sqlMethod;
        this.discriminator = builder.discriminator;
        this.poClass = builder.poClass;
        this.initCause = builder.initCause;
    }

    public static Builder builder(Class<?> repositoryClass, String repositoryMethodName, SqlMethod sqlMethod) {
        return new Builder(repositoryClass, repositoryMethodName, sqlMethod);
    }

    @Nonnull
    @Override
    public Class<?> getRepositoryClass() {
        return repositoryClass;
    }

    @Nonnull
    @Override
    public String getRepositoryMethodName() {
        return repositoryMethodName;
    }

    @Nonnull
    @Override
    public List<Param> getRepositoryMethodParams() {
        return Collections.unmodifiableList(repositoryMethodParams);
    }

    @Nonnull
    @Override
    public SqlMethod getSqlMethod() {
        return sqlMethod;
    }

    @Nullable
    @Override
    public Class<?> getPoClass() {
        return poClass;
    }

    @Nullable
    @Override
    public Exception getInitCause() {
        return initCause;
    }

    @Nullable
    @Override
    public String getDiscriminator() {
        return discriminator;
    }

    public static class Builder {
        private final Class<?> repositoryClass;
        private final String repositoryMethodName;
        private final SqlMethod sqlMethod;
        private final List<Param> repositoryMethodParams = new ArrayList<>();
        private String discriminator;
        private Class<?> poClass;
        private Exception initCause;

        public Builder(Class<?> repositoryClass, String repositoryMethodName, SqlMethod sqlMethod) {
            this.repositoryClass = argNotNull(repositoryClass, "repositoryClass");
            this.repositoryMethodName = argNotBlank(repositoryMethodName, "repositoryMethodName");
            this.sqlMethod = argNotNull(sqlMethod, "sqlMethod");
        }

        public Builder addParam(String paramName, Object paramValue) {
            repositoryMethodParams.add(new Param(paramName, paramValue));
            return this;
        }

        public Builder addParam(String paramName, Class<?> paramClass, Object paramValue) {
            repositoryMethodParams.add(new Param(paramName, paramClass, paramValue));
            return this;
        }

        public Builder addParam(Map<String,Object> map) {
            argNotNull(map,"map").keySet().forEach(k->addParam(k,map.get(k)));
            return this;
        }

        public Builder addParam(Map.Entry<String,Object> entry){
            addParam(argNotNull(entry,"entry").getKey(),entry.getValue());
            return this;
        }


        public Builder setPoClass(Class<?> poClass) {
            this.poClass = argNotNull(poClass, "poClass");
            return this;
        }

        public Builder setInitCause(Exception initCause) {
            this.initCause = argNotNull(initCause, "initCause");
            return this;
        }

        public Builder setDiscriminator(String discriminator) {
            this.discriminator = argNotBlank(discriminator, "discriminator");
            return this;
        }

        public DefaultRepositoryContext build() {
            return new DefaultRepositoryContext(this);
        }
    }
}
