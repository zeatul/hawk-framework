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
 * <p>Provides thread-pool-based asynchronous task execution, supporting task submission, callbacks, timeout control, and other features.</p>
 *
 * @author Zhang Peng
 */
public class DefaultAsyncExecutor implements AsyncExecutor {

    private static final Logger LOG = LoggerFactory.getLogger(DefaultAsyncExecutor.class);
    private final ExecutorService executorService;
    private final ScheduledExecutorService scheduledExecutorService;
    private final String name;

    /**
     * Constructs an asynchronous executor (with a custom thread pool).
     *
     * @param name            the asynchronous executor name
     * @param executorService the custom thread pool
     */
    public DefaultAsyncExecutor(@Nonnull String name, @Nonnull ExecutorService executorService) {
        this.executorService = argNotNull(executorService, "executorService");
        this.name = name;
        // If the supplied thread pool supports scheduled tasks, use it directly; otherwise create a separate single-thread scheduler
        if (executorService instanceof ScheduledExecutorService) {
            this.scheduledExecutorService = (ScheduledExecutorService) executorService;
        } else {
            this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(
                r -> {
                    Thread t = new Thread(r, name + "-scheduler");
                    t.setDaemon(true);
                    return t;
                }
            );
        }
    }

    /**
     * Constructs an asynchronous executor (with a custom thread pool).
     *
     * @param executorService the custom thread pool
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
        ScheduledFuture<?> scheduled = scheduledExecutorService.schedule(() -> {
            try {
                T result = task.get();
                future.complete(result);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        }, delay, unit);

        // If the future is cancelled, also cancel the scheduled task
        future.whenComplete((r, ex) -> scheduled.cancel(false));

        return new AsyncResult<>(future);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task,
                                                  long initialDelay,
                                                  long period,
                                                  TimeUnit unit) {
        return scheduledExecutorService.scheduleAtFixedRate(task, initialDelay, period, unit);
    }

    /**
     * Shuts down the thread pool.
     * <p>
     * No longer accepts new tasks; waits for existing tasks to complete
     * </p>
     */
    public void shutdown() {
        LOG.info("Shutting down async executor: {}", name);
        executorService.shutdown();
        if (scheduledExecutorService != executorService) {
            scheduledExecutorService.shutdown();
        }
    }

    /**
     * Shuts down the thread pool immediately.
     * <p>
     * Attempts to stop all currently executing tasks
     * </p>
     */
    public void shutdownNow() {
        LOG.info("Force shutting down async executor: {}", name);
        executorService.shutdownNow();
        if (scheduledExecutorService != executorService) {
            scheduledExecutorService.shutdownNow();
        }
    }

    /**
     * Graceful shutdown (with timeout).
     *
     * @param timeout the timeout
     * @param unit    the time unit
     * @return true if all tasks have completed, otherwise false
     * @throws InterruptedException if the wait is interrupted
     */
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
        executorService.shutdown();
        if (scheduledExecutorService != executorService) {
            scheduledExecutorService.shutdown();
        }
        boolean execDone = executorService.awaitTermination(timeout, unit);
        if (scheduledExecutorService != executorService) {
            return execDone && scheduledExecutorService.awaitTermination(timeout, unit);
        }
        return execDone;
    }

    /**
     * Gets the thread pool status.
     *
     * @return thread pool status information
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
     * Checks whether it has been shut down.
     */
    public boolean isShutdown() {
        return executorService.isShutdown();
    }
}
