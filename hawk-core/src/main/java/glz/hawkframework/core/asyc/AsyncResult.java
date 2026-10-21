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
import javax.annotation.Nullable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * This class is responsible for
 * <p>Wraps CompletableFuture, providing convenient result retrieval and callback methods.
 * <p>Preserves exception stack traces and supports cancellation propagation.</p>
 *
 * @author Zhang Peng
 */
public class AsyncResult<T> {
    private final CompletableFuture<T> completableFuture;

    /**
     * Constructs an AsyncResult.
     *
     * @param completableFuture the CompletableFuture instance, must not be null
     */
    public AsyncResult(@Nonnull CompletableFuture<T> completableFuture) {
        this.completableFuture = completableFuture;
    }

    /**
     * Blocks waiting for the result, preserving the original exception stack trace.
     *
     * @return the task result
     * @throws ExecutionException   if the task failed (the cause contains the original exception)
     * @throws InterruptedException if the wait is interrupted
     */
    public T get() throws ExecutionException, InterruptedException {
        try {
            return completableFuture.get();
        } catch (ExecutionException e) {
            // Preserve the original exception stack trace; do not wrap CompletionException
            Throwable cause = e.getCause();
            if (cause instanceof CompletionException) {
                throw new ExecutionException(cause.getCause());
            }
            throw e;
        }
    }

    /**
     * Blocks waiting for the result (with timeout), preserving the original exception stack trace.
     *
     * @param timeout the timeout
     * @param unit    the time unit
     * @return the task result
     * @throws ExecutionException   if the task failed
     * @throws InterruptedException if the wait is interrupted
     * @throws TimeoutException     if the timeout elapses
     */
    public T get(long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
        try {
            return completableFuture.get(timeout, unit);
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof CompletionException) {
                throw new ExecutionException(cause.getCause());
            }
            throw e;
        }
    }

    /**
     * Non-blocking retrieval of the result (may be null).
     *
     * @return the task result, or null if not yet completed
     */
    public T getNow() {
        return completableFuture.getNow(null);
    }

    /**
     * Checks whether the task is complete.
     */
    public boolean isDone() {
        return completableFuture.isDone();
    }

    /**
     * Checks whether the task was cancelled.
     */
    public boolean isCancelled() {
        return completableFuture.isCancelled();
    }

    /**
     * Checks whether the task completed normally (no exception, not cancelled).
     */
    public boolean isCompletedNormally() {
        return completableFuture.isDone() && !completableFuture.isCompletedExceptionally() && !completableFuture.isCancelled();
    }

    /**
     * Checks whether the task completed exceptionally.
     */
    public boolean isCompletedExceptionally() {
        return completableFuture.isCompletedExceptionally();
    }

    /**
     * Gets the exception cause (if completed exceptionally).
     *
     * @return the exception cause, or null if it did not complete exceptionally
     */
    @Nullable
    public Throwable getException() {
        if (completableFuture.isCompletedExceptionally()) {
            try {
                completableFuture.get();
                return null;
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof CompletionException) {
                    return cause.getCause();
                }
                return cause;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return e;
            }
        }
        return null;
    }

    /**
     * Cancels the task.
     * <p>Cancellation propagates to subsequent tasks in the chain</p>
     *
     * @param mayInterruptIfRunning whether to interrupt a task that is currently running
     * @return true if the cancellation succeeded
     */
    public boolean cancel(boolean mayInterruptIfRunning) {
        return completableFuture.cancel(mayInterruptIfRunning);
    }

    /**
     * Registers a success callback.
     *
     * @param consumer the result handler
     * @return this (supports chained calls)
     */
    public AsyncResult<T> onSuccess(Consumer<T> consumer) {
        completableFuture.thenAccept(consumer);
        return this;
    }

    /**
     * Registers a failure callback (preserving the original exception).
     *
     * @param consumer the exception handler
     * @return this (supports chained calls)
     */
    public AsyncResult<T> onFailure(Consumer<Throwable> consumer) {
        completableFuture.exceptionally(throwable -> {
            // Unwrap CompletionException and pass the original exception
            Throwable cause = throwable;
            if (throwable instanceof CompletionException) {
                cause = throwable.getCause();
            }
            consumer.accept(cause);
            return null;
        });
        return this;
    }

    /**
     * Registers both success and failure callbacks.
     *
     * @param successConsumer the success callback
     * @param failureConsumer the failure callback
     * @return this (supports chained calls)
     */
    public AsyncResult<T> onComplete(Consumer<T> successConsumer, Consumer<Throwable> failureConsumer) {
        completableFuture.whenComplete((result, throwable) -> {
            if (throwable != null) {
                Throwable cause = throwable;
                if (throwable instanceof CompletionException) {
                    cause = throwable.getCause();
                }
                failureConsumer.accept(cause);
            } else {
                successConsumer.accept(result);
            }
        });
        return this;
    }

    /**
     * Transforms the result.
     *
     * @param mapper the mapping function
     * @param <R>    the new result type
     * @return the new AsyncResult
     */
    public <R> AsyncResult<R> thenApply(Function<T, R> mapper) {
        return new AsyncResult<>(completableFuture.thenApply(mapper));
    }

    /**
     * Chains the execution of another asynchronous task.
     * <p>Cancellation propagates automatically to subsequent tasks</p>
     *
     * @param mapper the function returning a new AsyncResult
     * @param <R>    the new result type
     * @return the new AsyncResult
     */
    public <R> AsyncResult<R> thenCompose(Function<T, AsyncResult<R>> mapper) {
        CompletableFuture<R> composed = completableFuture.thenCompose(result -> {
            AsyncResult<R> asyncResult = mapper.apply(result);
            return asyncResult.completableFuture;
        });
        return new AsyncResult<>(composed);
    }

    /**
     * Gets the underlying CompletableFuture.
     */
    public CompletableFuture<T> getFuture() {
        return completableFuture;
    }
}
