/*
 * MIT License
 *
 * Copyright (c) 2022 DreamCoins
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package net.code4me.anoduels.common.plugin.scheduler;

import net.code4me.anoduels.api.model.logger.Logger;
import net.code4me.anoduels.common.plugin.scheduler.concurrent.forkjoin.ForkJoinPoolBuilder;
import net.code4me.anoduels.common.plugin.scheduler.concurrent.ConcurrentThreadFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.*;

public abstract class AbstractTaskExecutor<E extends ExecutorService> {
    private static final int DEFAULT_POOL_SIZE = 1;
    private static final int DEFAULT_AWAIT_TERMINATION_SECONDS = 60;

    private int awaitTerminationSeconds = DEFAULT_AWAIT_TERMINATION_SECONDS;

    @Nullable
    private ForkJoinPool workerPool;

    private final Logger logger;

    @NotNull
    private final E executor;

    protected AbstractTaskExecutor(Logger logger) {
        this(logger, DEFAULT_POOL_SIZE, new ConcurrentThreadFactory(true));
    }

    protected AbstractTaskExecutor(Logger logger, @NotNull ThreadFactory threadFactory) {
        this(logger, DEFAULT_POOL_SIZE, threadFactory);
    }

    protected AbstractTaskExecutor(Logger logger, int poolSize, @NotNull ThreadFactory threadFactory) {
        this.logger = logger;
        this.executor = createExecutor(poolSize, threadFactory);
    }

    protected AbstractTaskExecutor(Logger logger, @NotNull E executor) {
        this.logger = logger;
        this.executor = executor;
    }

    protected abstract E createExecutor(int poolSize, @NotNull ThreadFactory threadFactory);

    public void execute(@NotNull Runnable task, boolean async) {
        if (async && workerPool != null) {
            this.workerPool.execute(task);
        } else {
            this.executor.execute(task);
        }
    }

    @NotNull
    public E getExecutor() {
        return this.executor;
    }

    public void setAwaitTerminationSeconds(int awaitTerminationSeconds) {
        this.awaitTerminationSeconds = awaitTerminationSeconds;
    }

    @NotNull
    public ForkJoinPoolBuilder createWorkerPoolBuilder() {
        return new ForkJoinPoolBuilder(this.logger);
    }

    @Nullable
    public ForkJoinPool getWorkerPool() {
        return this.workerPool;
    }

    public void setWorkerPool(@NotNull ForkJoinPool workerPool) {
        this.workerPool = workerPool;
    }

    public boolean isAsyncable() {
        return this.workerPool != null;
    }

    public void shutdownExecutor() {
        shutdown(this.executor);
    }

    public void shutdownWorkerPool() {
        if (this.workerPool != null) {
            shutdown(this.workerPool);
        }
    }

    private void shutdown(@NotNull ExecutorService executor) {
        executor.shutdown();
        try {
            if (!executor.awaitTermination(this.awaitTerminationSeconds, TimeUnit.SECONDS)) {
                this.logger.severe("Task executor did not terminate in the specified time.");
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.getAllStackTraces().keySet().stream()
                    .filter(thread -> thread.getName().startsWith("anoduels-thread-"))
                    .forEach(thread -> {
                        this.logger.severe("Thread " + thread.getName() + " is still running. Interrupting...");
                        thread.interrupt();
                    });
        }
    }
}
