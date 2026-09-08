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

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is responsible for
 * <p>封装 CompletableFuture，提供便捷的结果获取和回调方法。</p>
 *
 * @author Zhang Peng
 */
public class AsyncResult<T> {
    private final CompletableFuture<T> completableFuture;

    /**
     * 构造 AsyncResult
     *
     * @param completableFuture CompletableFuture 实例，不可为空
     */
    public AsyncResult(@Nonnull CompletableFuture<T> completableFuture) {
        this.completableFuture = completableFuture;
    }

    /**
     * 阻塞等待结果
     *
     * @return 任务结果
     * @throws ExecutionException   如果任务执行异常
     * @throws InterruptedException 如果等待被中断
     */
    public T get() throws ExecutionException, InterruptedException {
        return completableFuture.get();
    }

    /**
     * 阻塞等待结果（带超时）
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return 任务结果
     * @throws ExecutionException   如果任务执行异常
     * @throws InterruptedException 如果等待被中断
     * @throws TimeoutException     如果超时
     */
    public T get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        return completableFuture.get(timeout, unit);
    }

    /**
     * 非阻塞获取结果（可能为 null）
     *
     * @return 任务结果，如果未完成则返回 null
     */
    public T getNow() {
        return completableFuture.getNow(null);
    }

    /**
     * 检查任务是否完成
     */
    public boolean isDone() {
        return completableFuture.isDone();
    }

    /**
     * 检查任务是否被取消
     */
    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    /**
     * 检查任务是否正常完成（无异常）
     */
    public boolean isCompletedNormally() {
        return completableFuture.isDone() && !completableFuture.isCompletedExceptionally() && !completableFuture.isCancelled();
    }

    /**
     * 取消任务
     *
     * @param mayInterruptIfRunning 是否中断正在执行的任务
     * @return true 如果取消成功
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * 注册成功回调
     *
     * @param consumer 结果处理器
     * @return this（支持链式调用）
     */
    public AsyncResult<T> onSuccess(Consumer<T> consumer) {
        completableFuture.thenAccept(consumer);
        return this;
    }

    /**
     * 注册失败回调
     *
     * @param consumer 异常处理器
     * @return this（支持链式调用）
     */
    public AsyncResult<T> onFailure(Consumer<Throwable> consumer) {
        completableFuture.exceptionally(throwable -> {
            consumer.accept(throwable);
            return null;
        });
        return this;
    }

    /**
     * 注册成功和失败回调
     *
     * @param successConsumer 成功回调
     * @param failureConsumer 失败回调
     * @return this（支持链式调用）
     */
    public AsyncResult<T> onComplete(Consumer<T> successConsumer, Consumer<Throwable> failureConsumer) {
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                failureConsumer.accept(throwable);
            } else {
                successConsumer.accept(result);
            }
        });
        return this;
    }

    /**
     * 转换结果
     *
     * @param mapper 转换函数
     * @param <R>    新结果类型
     * @return 新的 AsyncResult
     */
    public <R> AsyncResult<R> thenApply(Function<T, R> mapper) {
        return new AsyncResult<>(completableFuture.thenApplyAsync(mapper));
    }

    /**
     * 链式执行另一个异步任务
     *
     * @param mapper 返回新 AsyncResult 的函数
     * @param <R>    新结果类型
     * @return 新的 AsyncResult
     */
    public <R> AsyncResult<R> thenCompose(Function<T, AsyncResult<R>> mapper) {
        CompletableFuture<R> composed = completableFuture.thenCompose(result -> {
            AsyncResult<R> asyncResult = mapper.apply(result);
            return asyncResult.completableFuture;
        });
        return new AsyncResult<>(composed);
    }


    /**
     * 获取原始的 CompletableFuture
     */
    public CompletableFuture<T> getFuture() {
        return completableFuture;
    }
}
