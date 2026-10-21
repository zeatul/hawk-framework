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

package glz.hawkframework.core.asyc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public abstract class AsyncResultUtils {
    /**
     * Waits for multiple asynchronous tasks to complete.
     *
     * @param results multiple AsyncResults
     * @return a new AsyncResult (of type Void)
     */
    public static AsyncResult<Void> allOf(AsyncResult<?>... results) {
        CompletableFuture<?>[] futures = new CompletableFuture[results.length];
        for (int i = 0; i < results.length; i++) {
            futures[i] = results[i].getFuture();
        }
        return new AsyncResult<>(CompletableFuture.allOf(futures));
    }

    /**
     * Waits for all tasks to complete and collects all results.
     *
     * @param results multiple AsyncResults
     * @param <T>     the result type
     * @return the list of results from all tasks
     * @throws ExecutionException   if a task failed
     * @throws InterruptedException if the wait is interrupted
     */
    @SafeVarargs
    public static <T> List<T> getAllResults(AsyncResult<T>... results) throws ExecutionException, InterruptedException {
        // Wait for all to complete
        allOf(results).get();
        // Collect the results
        List<T> resultList = new ArrayList<>();
        for (AsyncResult<T> result : results) {
            resultList.add(result.get());
        }
        return resultList;
    }

    /**
     * Waits for all tasks to complete and collects all results (with timeout).
     */
    @SafeVarargs
    public static <T> List<T> getAllResults(long timeout, TimeUnit unit, AsyncResult<T>... results) throws ExecutionException, InterruptedException, TimeoutException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);
        allOf(results).get(timeout, unit);
        List<T> resultList = new ArrayList<>();
        for (AsyncResult<T> result : results) {
            long remaining = deadline - System.nanoTime();
            if (remaining <= 0) throw new TimeoutException();
            resultList.add(result.get(remaining, TimeUnit.NANOSECONDS));
        }
        return resultList;
    }

    /**
     * Waits for all tasks to complete and collects all results (using join).
     */
    @SafeVarargs
    public static <T> List<T> getAllResultsJoin(AsyncResult<T>... results) {
        allOf(results).getFuture().join();
        List<T> resultList = new ArrayList<>();
        for (AsyncResult<T> result : results) {
            resultList.add(result.getFuture().join());
        }
        return resultList;
    }

    /**
     * Registers callbacks in bulk (executed after all tasks complete).
     */
    @SafeVarargs
    public static <T> AsyncResult<List<T>> allOfWithCallback(Consumer<List<T>> successCallback, Consumer<Throwable> failureCallback, AsyncResult<T>... results) {
        CompletableFuture<List<T>> combined = CompletableFuture
            .allOf(Arrays.stream(results).map(AsyncResult::getFuture).toArray(CompletableFuture[]::new))
            .thenApply(v -> Arrays.stream(results)
                .map(r -> r.getFuture().join())
                .collect(Collectors.toList()));
        combined.whenComplete((list, throwable) -> {
            if (throwable != null) {
                failureCallback.accept(throwable);
            } else {
                successCallback.accept(list);
            }
        });
        return new AsyncResult<>(combined);
    }

    /**
     * Waits for any one of the asynchronous tasks to complete.
     *
     * @param results multiple AsyncResults
     * @return a new AsyncResult
     */
    public static AsyncResult<Object> anyOf(AsyncResult<?>... results) {
        CompletableFuture<?>[] futures = new CompletableFuture[results.length];
        for (int i = 0; i < results.length; i++) {
            futures[i] = results[i].getFuture().thenApply(Function.identity());
        }
        return new AsyncResult<>(CompletableFuture.anyOf(futures));
    }

}
