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

import java.util.concurrent.*;

/**
 * This class is responsible for
 * <p></p>
 *
 * @author Zhang Peng
 */
public class ExecutorServiceBuilder {

    private int corePoolSize = Runtime.getRuntime().availableProcessors();
    private int maximumPoolSize = corePoolSize * 2;
    private long keepAliveTime = 60L;
    private TimeUnit unit = TimeUnit.SECONDS;
    private BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(1000);
    private ThreadFactory threadFactory = Executors.defaultThreadFactory();
    private RejectedExecutionHandler handler = new ThreadPoolExecutor.CallerRunsPolicy();

    private ExecutorServiceBuilder() {
    }

    public static ExecutorServiceBuilder builder() {
        return new ExecutorServiceBuilder();
    }

    public ExecutorServiceBuilder setCorePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ExecutorServiceBuilder setMaximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public ExecutorServiceBuilder setKeepAliveTime(long keepAliveTime, TimeUnit unit) {
        this.keepAliveTime = keepAliveTime;
        this.unit = unit;
        return this;
    }

    public ExecutorServiceBuilder setWorkQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ExecutorServiceBuilder setHandler(RejectedExecutionHandler handler){
        this.handler = handler;
        return this;
    }

    public ExecutorService build() {
        return new ThreadPoolExecutor(
            corePoolSize,
            maximumPoolSize,
            keepAliveTime,
            unit,
            workQueue,
            threadFactory,
            handler
        );
    }
}
