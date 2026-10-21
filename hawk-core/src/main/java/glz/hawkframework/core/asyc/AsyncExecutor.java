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
 * <p>Provides thread-pool-based asynchronous task execution, supporting task submission, callbacks, timeout control, and other features.</p>
 *
 * @author Zhang Peng
 */
public interface AsyncExecutor {


    /**
     * Submits an asynchronous task with no return value.
     *
     * @param task the task to execute
     * @return AsyncResult used to observe task status
     */
    AsyncResult<Void> submit(@Nonnull Runnable task);

    /**
     * Submits an asynchronous task (with a return value).
     *
     * @param task the task to execute
     * @param <T>  the return type
     * @return AsyncResult used to obtain the result
     */
    <T> AsyncResult<T> submit(Supplier<T> task);

    /**
     * Submits an asynchronous task (which may throw an exception).
     *
     * @param task the task to execute (which may throw an exception)
     * @param <T>  the return type
     * @return AsyncResult used to obtain the result
     */
    <T> AsyncResult<T> submitThrows(ThrowingSupplier<T> task);

    /**
     * Submits a delayed task.
     *
     * @param task  the task to execute
     * @param delay the delay time
     * @param unit  the time unit
     * @param <T>   the return type
     * @return AsyncResult used to obtain the result
     */
    <T> AsyncResult<T> submitDelayed(Supplier<T> task, long delay, TimeUnit unit);

    /**
     * Submits a periodic task (using a ScheduledExecutorService).
     *
     * @param task         the task to execute
     * @param initialDelay the initial delay
     * @param period       the execution period
     * @param unit         the time unit
     * @return ScheduledFuture used to cancel the task
     */
    ScheduledFuture<?> scheduleAtFixedRate(Runnable task, long initialDelay, long period, TimeUnit unit) ;

}
