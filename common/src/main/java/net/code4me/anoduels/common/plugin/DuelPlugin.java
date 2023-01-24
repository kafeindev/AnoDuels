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

package net.code4me.anoduels.common.plugin;

import com.github.kafeintr.commands.common.command.CommandManager;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.managers.*;
import net.code4me.anoduels.api.model.logger.Logger;
import net.code4me.anoduels.common.plugin.scheduler.concurrent.ConcurrentTaskScheduler;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public interface DuelPlugin {
    void load();

    void enable();

    void disable();

    void loadConfigs();

    @NotNull
    ConcurrentTaskScheduler getTaskScheduler();

    @NotNull
    Audience getAudience();

    @NotNull
    Path getDataPath();

    @NotNull
    Logger getLogger();

    @NotNull
    ConfigManager getConfigManager();

    @NotNull
    CommandManager<String> getCommandManager();

    @NotNull
    UserManager getUserManager();

    @NotNull
    MenuManager getMenuManager();

    @NotNull
    InviteManager getInviteManager();

    @NotNull
    QueueManager getQueueManager();

    @NotNull
    MatchManager getMatchManager();

    @NotNull
    HistoryManager getHistoryManager();

    @NotNull
    KitManager getKitManager();

    @NotNull
    ArenaManager getArenaManager();

    @NotNull
    ItemComponent.ItemCreator getItemCreator();
}
