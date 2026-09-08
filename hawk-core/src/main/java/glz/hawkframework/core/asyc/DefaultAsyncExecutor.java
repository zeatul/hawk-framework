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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.util.concurrent.*;
import java.util.function.Supplier;

import static glz.hawkframework.core.support.ArgumentSupport.argNotNull;

/**
 * This class is responsible for
 * <p>提供基于线程池的异步任务执行能力，支持任务提交、回调、超时控制等功能。</p>
 *
 * @author Zhang Peng
 */
public class DefaultAsyncExecutor implements AsyncExecutor{

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAsyncExecutor.class);
    private final ExecutorService executorService;
    private final String name;

    /**
     * 构造异步执行器（自定义线程池）
     *
     * @param name            异步执行器名称
     * @param executorService 自定义线程池
     */
    public DefaultAsyncExecutor(@Nonnull String name, @Nonnull ExecutorService executorService) {
        this.executorService = argNotNull(executorService, "executorService");
        this.name = name;
    }

    /**
     * 构造异步执行器（自定义线程池）
     *
     * @param executorService 自定义线程池
     */
    public DefaultAsyncExecutor(@Nonnull ExecutorService executorService) {
        this("DefaultAsyncExecutor", executorService);
    }

    @Override
    public AsyncResult<Void> submit(@Nonnull Runnable task) {
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(task, executorService);
        return new AsyncResult<>(completableFuture);
    }

    @Override
    public <T> AsyncResult<T> submit(Supplier<T> task) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(task, executorService);
        return new AsyncResult<>(future);
    }

    @Override
    public <T> AsyncResult<T> submitThrows(ThrowingSupplier<T> task) {
        CompletableFuture<T> future = CompletableFuture.supplyAsync(() -> {
            try {
                return task.get();
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executorService);
        return new AsyncResult<>(future);
    }

    @Override
    public <T> AsyncResult<T> submitDelayed(Supplier<T> task, long delay, TimeUnit unit) {
        CompletableFuture<T> future = new CompletableFuture<>();
        executorService.submit(() -> {
            try {
                Thread.sleep(unit.toMillis(delay));
                T result = task.get();
                future.complete(result);
            } catch (InterruptedException e) {
                future.completeExceptionally(e);
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return new AsyncResult<>(future);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task,
                                                  long initialDelay,
                                                  long period,
                                                  TimeUnit unit) {
        if (!(executorService instanceof ScheduledExecutorService)) {
            throw new UnsupportedOperationException("当前线程池不支持周期性任务，请使用 ScheduledExecutorService");
        }
        return ((ScheduledExecutorService) executorService).scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * 关闭线程池
     * <p>
     * 不再接受新任务，等待已有任务完成
     * </p>
     */
    public void shutdown() {
        LOG.info("正在关闭异步执行器: {}", name);
        executorService.shutdown();
    }

    /**
     * 立即关闭线程池
     * <p>
     * 尝试停止所有正在执行的任务
     * </p>
     */
    public void shutdownNow() {
        LOG.info("正在强制关闭异步执行器: {}", name);
        executorService.shutdownNow();
    }

    /**
     * 优雅关闭（带超时）
     *
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true 如果所有任务已完成，否则 false
     * @throws InterruptedException 如果等待被中断
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        executorService.shutdown();
        return executorService.awaitTermination(timeout, unit);
    }

    /**
     * 获取线程池状态
     *
     * @return 线程池状态信息
     */
    public String getPoolStatus() {
        if (executorService instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor pool = (ThreadPoolExecutor) executorService;
            return String.format(
                "Active: %d, PoolSize: %d, CoreSize: %d, MaxSize: %d, QueueSize: %d, Completed: %d",
                pool.getActiveCount(),
                pool.getPoolSize(),
                pool.getCorePoolSize(),
                pool.getMaximumPoolSize(),
                pool.getQueue().size(),
                pool.getCompletedTaskCount()
            );
        }
        return "Unknown pool type: " + executorService.getClass().getSimpleName();
    }

    /**
     * 检查是否已关闭
     */
    public boolean isShutdown() {
        return executorService.isShutdown();
    }
}
