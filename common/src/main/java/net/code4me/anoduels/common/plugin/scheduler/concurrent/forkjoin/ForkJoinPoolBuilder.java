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

package net.code4me.anoduels.common.plugin.scheduler.concurrent.forkjoin;

import net.code4me.anoduels.api.model.logger.Logger;

import java.util.concurrent.ForkJoinPool;

public final class ForkJoinPoolBuilder {
    private static final int DEFAULT_PARALLELISM = 16;

    private final Logger logger;

    private int parallelism = DEFAULT_PARALLELISM;

    private boolean asyncMode;
    private boolean daemon;

    public ForkJoinPoolBuilder(Logger logger) {
        this.logger = logger;
    }

    public ForkJoinPoolBuilder setParallelism(int parallelism) {
        this.parallelism = parallelism;
        return this;
    }

    public ForkJoinPoolBuilder setAsyncMode(boolean asyncMode) {
        this.asyncMode = asyncMode;
        return this;
    }

    public ForkJoinPoolBuilder setDaemon(boolean daemon) {
        this.daemon = daemon;
        return this;
    }

    public ForkJoinPool build() {
        return new ForkJoinPool(parallelism,
                new WorkerThreadFactory(daemon),
                (t, e) -> logger.severe("Uncaught exception in thread " + t.getName()),
                asyncMode);
    }
}
