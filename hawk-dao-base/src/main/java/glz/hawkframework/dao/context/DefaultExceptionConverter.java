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

import glz.hawkframework.dao.exception.ConcurrentModifyException;
import glz.hawkframework.dao.exception.MultipleRecordsFoundException;
import glz.hawkframework.dao.exception.RecordNotFoundException;
import glz.hawkframework.dao.exception.UniqueViolationException;
import org.springframework.dao.DuplicateKeyException;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 *
 * @author Hawk
 */
public class DefaultExceptionConverter implements ExceptionConverter {


    private final Function<Map<String, Object>, String> concurrentModifierExceptionMessageSupplier;


    private final Function<Map<String, Object>, String> recordNotFoundExceptionMessageSupplier;


    private final Function<Map<String, Object>, String> multipleRecordsFoundExceptionMessageSupplier;


    private final Function<Map<String, Object>, String> uniqueViolationExceptionMessageSupplier;

    public DefaultExceptionConverter(Function<Map<String, Object>, String> concurrentModifierExceptionMessageSupplier,
                                     Function<Map<String, Object>, String> recordNotFoundExceptionMessageSupplier,
                                     Function<Map<String, Object>, String> multipleRecordsFoundExceptionMessageSupplier,
                                     Function<Map<String, Object>, String> uniqueViolationExceptionMessageSupplier) {

        this.recordNotFoundExceptionMessageSupplier = recordNotFoundExceptionMessageSupplier == null ? RecordNotFoundException.DEFAULT_MESSAGE_SUPPLIER : recordNotFoundExceptionMessageSupplier;
        this.multipleRecordsFoundExceptionMessageSupplier = multipleRecordsFoundExceptionMessageSupplier == null ? MultipleRecordsFoundException.DEFAULT_MESSAGE_SUPPLIER : multipleRecordsFoundExceptionMessageSupplier;
        this.concurrentModifierExceptionMessageSupplier = concurrentModifierExceptionMessageSupplier == null ? ConcurrentModifyException.DEFAULT_MESSAGE_SUPPLIER : concurrentModifierExceptionMessageSupplier;
        this.uniqueViolationExceptionMessageSupplier = uniqueViolationExceptionMessageSupplier == null ? UniqueViolationException.DEFAULT_MESSAGE_SUPPLIER : uniqueViolationExceptionMessageSupplier;
    }

    protected Map<String, Object> params(RepositoryContext repositoryContext) {
        return repositoryContext.getRepositoryMethodParams().stream().collect(Collectors.toMap(Param::getParamName, Param::getParamValues));
    }


    @Override
    public RuntimeException convertTo(RepositoryContext repositoryContext) {
        argNotNull(repositoryContext, "repositoryContext");
        Exception initCause = repositoryContext.getInitCause();
        if (initCause != null) {
            if (initCause instanceof DuplicateKeyException) {
                return new UniqueViolationException(initCause, uniqueViolationExceptionMessageSupplier, repositoryContext.getPoClass(), params(repositoryContext));
            }else if (initCause instanceof SQLIntegrityConstraintViolationException){
                SQLIntegrityConstraintViolationException e = (SQLIntegrityConstraintViolationException) initCause;

            }
            else if (initCause instanceof RuntimeException) {
                return (RuntimeException) initCause;
            } else {
                return new RuntimeException(initCause);
            }
        }

        argNotNull(repositoryContext.getSqlMethod(), "repositoryContext.sqlMethod");

        switch (repositoryContext.getSqlMethod()) {
            case UPDATE:
            case DELETE:
                if (RepositoryContext.AFFECTED_ROWS_COUNT_IS_UNEXPECTED.equals(repositoryContext.getDiscriminator())) {
                    return new ConcurrentModifyException(concurrentModifierExceptionMessageSupplier, repositoryContext.getPoClass(), params(repositoryContext));
                }
                break;
            case SELECT:
                if (RepositoryContext.FOUND_NONE.equals(repositoryContext.getDiscriminator())) {
                    return new RecordNotFoundException(recordNotFoundExceptionMessageSupplier, repositoryContext.getPoClass(), params(repositoryContext));
                } else if (RepositoryContext.FOUND_MULTIPLE.equals(repositoryContext.getDiscriminator())) {
                    return new MultipleRecordsFoundException(multipleRecordsFoundExceptionMessageSupplier, repositoryContext.getPoClass(), params(repositoryContext));
                }
                break;
            default:
                // do nothing
        }
        return new RuntimeException("Can't convert the repositoryContext to the exception");
    }
}
