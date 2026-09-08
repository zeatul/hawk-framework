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
     * 等待多个异步任务完成
     *
     * @param results 多个 AsyncResult
     * @return 新的 AsyncResult（Void 类型）
     */
    public static AsyncResult<Void> allOf(AsyncResult<?>... results) {
        CompletableFuture<?>[] futures = new CompletableFuture[results.length];
        for (int i = 0; i < results.length; i++) {
            futures[i] = results[i].getFuture();
        }
        return new AsyncResult<>(CompletableFuture.allOf(futures));
    }

    /**
     * 等待所有任务完成，并收集所有结果
     *
     * @param results 多个 AsyncResult
     * @param <T>     结果类型
     * @return 所有任务的结果列表
     * @throws ExecutionException   如果任务执行异常
     * @throws InterruptedException 如果等待被中断
     */
    @SafeVarargs
    public static <T> List<T> getAllResults(AsyncResult<T>... results) throws ExecutionException, InterruptedException {
        // 等待所有完成
        allOf(results).get();
        // 收集结果
        List<T> resultList = new ArrayList<>();
        for (AsyncResult<T> result : results) {
            resultList.add(result.get());
        }
        return resultList;
    }

    /**
     * 等待所有任务完成，并收集所有结果（带超时）
     */
    @SafeVarargs
    public static <T> List<T> getAllResults(long timeout, TimeUnit unit, AsyncResult<T>... results) throws ExecutionException, InterruptedException, TimeoutException {
        allOf(results).get(timeout, unit);
        List<T> resultList = new ArrayList<>();
        for (AsyncResult<T> result : results) {
            resultList.add(result.get());
        }
        return resultList;
    }

    /**
     * 等待所有任务完成，并收集所有结果（使用 join）
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
     * 批量注册回调（所有任务完成后执行）
     */
    @SafeVarargs
    public static <T> AsyncResult<List<T>> allOfWithCallback(Consumer<List<T>> successCallback, Consumer<Throwable> failureCallback, AsyncResult<T>... results) {
        CompletableFuture<List<T>> combined = CompletableFuture
            .allOf(Arrays.stream(results).map(AsyncResult::getFuture).toArray(CompletableFuture[]::new))
            .thenApply(v -> Arrays.stream(results)
                .map(r -> r.getFuture().join())
                .collect(Collectors.toList()))
            .exceptionally(throwable -> {
                failureCallback.accept(throwable);
                return new ArrayList<>();
            });
        return new AsyncResult<>(combined);
    }

    /**
     * 等待任意一个异步任务完成
     *
     * @param results 多个 AsyncResult
     * @return 新的 AsyncResult
     */
    public static AsyncResult<Object> anyOf(AsyncResult<?>... results) {
        CompletableFuture<?>[] futures = new CompletableFuture[results.length];
        for (int i = 0; i < results.length; i++) {
            futures[i] = results[i].getFuture().thenApply(Function.identity());
        }
        return new AsyncResult<>(CompletableFuture.anyOf(futures));
    }

}
