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
import java.util.concurrent.*;
import java.util.function.Supplier;

/**
 * This interface is responsible for
 * <p>提供基于线程池的异步任务执行能力，支持任务提交、回调、超时控制等功能。</p>
 *
 * @author Zhang Peng
 */
public interface AsyncExecutor {


    /**
     * 提交异步执行任务，无返回值
     *
     * @param task 要执行的任务
     * @return AsyncResult 用于监听任务状态
     */
    AsyncResult<Void> submit(@Nonnull Runnable task);

    /**
     * 提交异步任务（有返回值）
     *
     * @param task 要执行的任务
     * @param <T>  返回类型
     * @return AsyncResult 用于获取结果
     */
    <T> AsyncResult<T> submit(Supplier<T> task);

    /**
     * 提交异步任务（可抛出异常）
     *
     * @param task 要执行的任务（可抛出异常）
     * @param <T>  返回类型
     * @return AsyncResult 用于获取结果
     */
    <T> AsyncResult<T> submitThrows(ThrowingSupplier<T> task);

    /**
     * 提交延迟任务
     *
     * @param task  要执行的任务
     * @param delay 延迟时间
     * @param unit  时间单位
     * @param <T>   返回类型
     * @return AsyncResult 用于获取结果
     */
    <T> AsyncResult<T> submitDelayed(Supplier<T> task, long delay, TimeUnit unit);

    /**
     * 提交周期性任务（使用 ScheduledExecutorService）
     *
     * @param task         要执行的任务
     * @param initialDelay 初始延迟
     * @param period       执行周期
     * @param unit         时间单位
     * @return ScheduledFuture 用于取消任务
     */
    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) ;

}
