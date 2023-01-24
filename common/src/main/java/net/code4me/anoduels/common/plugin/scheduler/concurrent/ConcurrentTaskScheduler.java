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

package net.code4me.anoduels.common.plugin.scheduler.concurrent;

import net.code4me.anoduels.api.model.logger.Logger;
import net.code4me.anoduels.common.plugin.scheduler.AbstractTaskExecutor;
import net.code4me.anoduels.api.task.ScheduledTask;
import net.code4me.anoduels.api.task.TaskScheduler;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.*;

public final class ConcurrentTaskScheduler extends AbstractTaskExecutor<ScheduledThreadPoolExecutor> implements TaskScheduler {
    public ConcurrentTaskScheduler(Logger logger) {
        super(logger);
    }

    public ConcurrentTaskScheduler(Logger logger, @NotNull ThreadFactory threadFactory) {
        super(logger, threadFactory);
    }

    public ConcurrentTaskScheduler(Logger logger, int poolSize, @NotNull ThreadFactory threadFactory) {
        super(logger, poolSize, threadFactory);
    }

    @Override
    protected ScheduledThreadPoolExecutor createExecutor(int poolSize, @NotNull ThreadFactory threadFactory) {
        ScheduledThreadPoolExecutor executor = new ScheduledThreadPoolExecutor(poolSize, threadFactory);
        executor.setRemoveOnCancelPolicy(true);
        executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);

        return executor;
    }

    @Override
    public @NotNull ScheduledTask schedule(@NotNull Runnable task, long delay, boolean async) {
        ScheduledFuture<?> future = getExecutor().schedule(
                () -> execute(task, async), delay, TimeUnit.MILLISECONDS);

        return () -> future.cancel(false);
    }

    @Override
    public @NotNull ScheduledTask scheduleRepeating(@NotNull Runnable task, long delay, long period, boolean async) {
        ScheduledFuture<?> future = getExecutor().scheduleAtFixedRate(
                () -> execute(task, async), delay, period, TimeUnit.MILLISECONDS);

        return () -> future.cancel(false);
    }
}
