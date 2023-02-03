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

import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FileLogger extends AbstractLogger {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter DATE_FORMATTER_FOR_FILE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private final Queue<String> logQueue = new ConcurrentLinkedQueue<>();

    @NotNull
    private final DuelPlugin plugin;

    @NotNull
    private final Path dataPath;

    public FileLogger(@NotNull DuelPlugin plugin, @NotNull Logger logger, @NotNull Path dataPath) {
        super(logger);
        this.plugin = plugin;
        this.dataPath = dataPath;
        FileProcessor.processDirectory(dataPath);

        plugin.getTaskScheduler().scheduleRepeating(this::flush, Duration.ofSeconds(1L), true);
    }

    @Override
    protected void log(@NotNull String message, @NotNull Level level) {
        String dateTime = DATE_FORMATTER.format(LocalDateTime.now());

        String logMessage = String.format("[%s] [%s]: %s", dateTime, level.getName(), message);
        this.logQueue.add(logMessage);
    }

    private void flush() {
        if (this.logQueue.peek() == null) {
            return;
        }

        String dateTime = DATE_FORMATTER_FOR_FILE.format(LocalDateTime.now());
        Path logPath = this.dataPath.resolve(String.format("%s.log", dateTime));
        try {
            Files.write(logPath, this.logQueue, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.APPEND);

            this.logQueue.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        plugin.getTaskScheduler().execute(this::flush, true);
    }
}
