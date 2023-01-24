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
import net.code4me.anoduels.api.model.Config;
import net.code4me.anoduels.api.model.logger.Logger;
import net.code4me.anoduels.common.config.ConfigManagerImpl;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.common.managers.*;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.plugin.logger.FileLogger;
import net.code4me.anoduels.common.plugin.scheduler.concurrent.ConcurrentTaskScheduler;
import net.kyori.adventure.audience.Audience;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDuelPlugin implements DuelPlugin {
    private ConcurrentTaskScheduler taskScheduler;
    private Audience audience;
    private Logger logger;

    private ConfigManager configManager;
    private CommandManager<String> commandManager;
    private UserManager userManager;
    private MenuManager menuManager;
    private InviteManager inviteManager;
    private QueueManager queueManager;
    private HistoryManager historyManager;
    private MatchManager matchManager;
    private KitManager kitManager;
    private ArenaManager arenaManager;
    private ItemComponent.ItemCreator itemCreator;

    @Override
    public void load() {
        this.taskScheduler = createTaskScheduler();

        FileProcessor.processDirectory(getDataPath());
        this.logger = new FileLogger(this, getParentLogger(), getDataPath().resolve("logs"));
    }

    @Override
    public void enable() {
        this.audience = createAudience();

        this.configManager = new ConfigManagerImpl(getDataPath());
        loadConfigs();

        this.commandManager = createCommandManager();
        registerCommands();

        this.itemCreator = createItemCreator();

        this.menuManager = new MenuManagerImpl(this, createMenuCreator());
        this.menuManager.initialize();

        this.kitManager = new KitManagerImpl(this);
        this.kitManager.initialize();

        this.arenaManager = new ArenaManagerImpl(this);
        this.arenaManager.initialize();

        this.userManager = new UserManagerImpl();
        this.inviteManager = new InviteManagerImpl(this);
        this.queueManager = new QueueManagerImpl(this);
        this.historyManager = new HistoryManagerImpl();
        this.matchManager = new MatchManagerImpl();

        registerListeners();
    }

    @Override
    public void disable() {
        this.taskScheduler.shutdownExecutor();

        this.arenaManager.shutdown();
        this.kitManager.shutdown();

        getLogger().close();
        this.taskScheduler.shutdownWorkerPool();
    }

    @Override
    public void loadConfigs() {
        getLogger().info("Loading configs...");

        //settings
        Config settingsConfig = this.configManager.create("settings", "settings.yml", getClass(), "settings.yml");
        this.configManager.injectKeys(settingsConfig, ConfigKeys.Settings.class, true);
        this.configManager.put("settings", settingsConfig);

        //language
        Config languageConfig = this.configManager.create("language", "language.yml", getClass(), "language.yml");
        this.configManager.injectKeys(languageConfig, ConfigKeys.Language.class, true);
        this.configManager.put("language", languageConfig);
    }

    protected abstract void registerCommands();

    protected abstract void registerListeners();

    @NotNull
    protected abstract ConcurrentTaskScheduler createTaskScheduler();

    @NotNull
    protected abstract Audience createAudience();

    @NotNull
    protected abstract CommandManager<String> createCommandManager();

    @NotNull
    protected abstract ItemComponent.ItemCreator createItemCreator();

    @NotNull
    protected abstract MenuManagerImpl.MenuCreator createMenuCreator();

    @NotNull
    protected abstract java.util.logging.Logger getParentLogger();

    @Override
    public @NotNull ConcurrentTaskScheduler getTaskScheduler() {
        return this.taskScheduler;
    }

    @Override
    public @NotNull Audience getAudience() {
        return this.audience;
    }

    @Override
    public @NotNull Logger getLogger() {
        return this.logger;
    }

    @Override
    public @NotNull ConfigManager getConfigManager() {
        return this.configManager;
    }

    @Override
    public @NotNull CommandManager<String> getCommandManager() {
        return this.commandManager;
    }

    @Override
    public @NotNull UserManager getUserManager() {
        return this.userManager;
    }

    @Override
    public @NotNull MenuManager getMenuManager() {
        return this.menuManager;
    }

    @Override
    public @NotNull InviteManager getInviteManager() {
        return this.inviteManager;
    }

    @Override
    public @NotNull QueueManager getQueueManager() {
        return this.queueManager;
    }

    @Override
    public @NotNull MatchManager getMatchManager() {
        return this.matchManager;
    }

    @Override
    public @NotNull HistoryManager getHistoryManager() {
        return this.historyManager;
    }

    @Override
    public @NotNull KitManager getKitManager() {
        return this.kitManager;
    }

    @Override
    public @NotNull ArenaManager getArenaManager() {
        return this.arenaManager;
    }

    @Override
    public @NotNull ItemComponent.ItemCreator getItemCreator() {
        return this.itemCreator;
    }
}
