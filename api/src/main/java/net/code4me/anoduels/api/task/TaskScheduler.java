/*
 * MIT License
 *
 * Copyright (c) 2022-2023 DreamCoins
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

package net.code4me.anoduels.api.task;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;

public interface TaskScheduler {
    @NotNull
    default ScheduledTask schedule(@NotNull Runnable task, @NotNull Duration delay) {
        return schedule(task, delay, false);
    }

    @NotNull
    default ScheduledTask schedule(@NotNull Runnable task, @NotNull Duration delay, boolean async) {
        return schedule(task, delay.toMillis(), async);
    }

    @NotNull
    default ScheduledTask schedule(@NotNull Runnable task, long delay) {
        return schedule(task, delay, false);
    }

    @NotNull
    ScheduledTask schedule(@NotNull Runnable task, long delay, boolean async);

    @NotNull
    default ScheduledTask scheduleRepeating(@NotNull Runnable task, @NotNull Duration period) {
        return scheduleRepeating(task, period, false);
    }

    @NotNull
    default ScheduledTask scheduleRepeating(@NotNull Runnable task, @NotNull Duration period, boolean async) {
        return scheduleRepeating(task, Duration.ZERO, period, async);
    }

    @NotNull
    default ScheduledTask scheduleRepeating(@NotNull Runnable task, @NotNull Duration delay, @NotNull Duration period) {
        return scheduleRepeating(task, delay, period, false);
    }

    @NotNull
    default ScheduledTask scheduleRepeating(@NotNull Runnable task,
                                            @NotNull Duration delay, @NotNull Duration period,
                                            boolean async) {
        return scheduleRepeating(task, delay.toMillis(), period.toMillis(), async);
    }

    @NotNull
    default ScheduledTask scheduleRepeating(@NotNull Runnable task, long period) {
        return scheduleRepeating(task, period, false);
    }

    @NotNull
    default ScheduledTask scheduleRepeating(@NotNull Runnable task, long period, boolean async) {
        return scheduleRepeating(task, 0L, period, async);
    }

    @NotNull
    default ScheduledTask scheduleRepeating(@NotNull Runnable task, long delay, long period) {
        return scheduleRepeating(task, delay, period, false);
    }

    @NotNull
    ScheduledTask scheduleRepeating(@NotNull Runnable task, long delay, long period, boolean async);
}
