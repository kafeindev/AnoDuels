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

package net.code4me.anoduels.common.plugin.logger;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AbstractLogger implements net.code4me.anoduels.api.model.logger.Logger {
    @NotNull
    protected final Logger logger;

    protected AbstractLogger(@NotNull Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(@NotNull String message) {
        logger.info(message);

        log(message, Level.INFO);
    }

    @Override
    public void warn(@NotNull String message) {
        logger.warning(message);

        log(message, Level.WARNING);
    }

    @Override
    public void warn(@NotNull String message, @Nullable Throwable throwable) {
        logger.log(Level.WARNING, message, throwable);

        log(message, Level.WARNING);
    }

    @Override
    public void severe(@NotNull String message) {
        logger.severe(message);

        log(message, Level.SEVERE);
    }

    @Override
    public void severe(@NotNull String message, @Nullable Throwable throwable) {
        logger.log(Level.SEVERE, message, throwable);

        log(message, Level.SEVERE);
    }

    protected void log(@NotNull String message, @NotNull Level level) {

    }

    @Override
    public void close() {

    }
}
