package net.code4me.anoduels.bukkit;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandManager;
import com.google.common.collect.ImmutableList;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.task.TaskScheduler;
import net.code4me.anoduels.bukkit.commands.DuelCommand;
import net.code4me.anoduels.bukkit.commands.admin.DuelAdminCommand;
import net.code4me.anoduels.bukkit.commands.admin.DuelArenaCommand;
import net.code4me.anoduels.bukkit.commands.admin.DuelKitCommand;
import net.code4me.anoduels.bukkit.component.BukkitPlayerComponent;
import net.code4me.anoduels.bukkit.listener.MenuInteractionListener;
import net.code4me.anoduels.bukkit.listener.ConnectionListener;
import net.code4me.anoduels.bukkit.listener.PlayerListener;
import net.code4me.anoduels.bukkit.listener.registry.ListenerRegistry;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.plugin.AbstractDuelPlugin;
import net.code4me.anoduels.common.plugin.scheduler.concurrent.ConcurrentTaskScheduler;
import net.kyori.adventure.platform.AudienceProvider;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

public final class BukkitDuelPlugin extends AbstractDuelPlugin {
    private static final List<Class<?>> LISTENERS = ImmutableList.of(
            ConnectionListener.class, PlayerListener.class,
            MenuInteractionListener.class
    );

    @NotNull
    private final Plugin plugin;

    public BukkitDuelPlugin(@NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    protected void registerCommands() {
        getCommandManager().registerCommand(new DuelCommand(this));

        getCommandManager().registerCommand(new DuelAdminCommand(this));
        getCommandManager().registerCommand(new DuelArenaCommand(this));
        getCommandManager().registerCommand(new DuelKitCommand(this));
    }

    @Override
    protected void registerListeners() {
        ListenerRegistry.register(this, LISTENERS);
    }

    @Override
    protected @NotNull TaskScheduler createTaskScheduler() {
        ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(getLogger());

        ForkJoinPool forkJoinPool = scheduler.createWorkerPoolBuilder()
                .setDaemon(true)
                .setAsyncMode(true)
                .build();
        scheduler.setWorkerPool(forkJoinPool);

        return scheduler;
    }

    @Override
    protected @NotNull AudienceProvider createAudience() {
        return BukkitAudiences.create(plugin);
    }

    @Override
    protected @NotNull CommandManager createCommandManager() {
        BukkitCommandManager commandManager = new BukkitCommandManager(plugin);
        commandManager.getCommandContexts().registerIssuerAwareContext(PlayerComponent.class, resolver -> {
            CommandSender sender = resolver.getSender();
            if (!(sender instanceof Player)) {
                sender.sendMessage("This command can only be executed by a player.");
                return null;
            }

            return BukkitPlayerComponent.fromPlayer(this, resolver.getPlayer());
        });

        return commandManager;
    }

    @Override
    protected @NotNull ItemComponent.ItemFactory<ItemStack> createItemFactory() {
        return new BukkitItemFactory();
    }

    @Override
    protected @NotNull PlayerComponent.PlayerFactory createPlayerFactory() {
        return new BukkitPlayerFactory(this);
    }

    @Override
    protected @NotNull MenuManagerImpl.MenuCreator createMenuCreator() {
        return new BukkitMenuCreator(this);
    }

    @Override
    protected @NotNull Logger getParentLogger() {
        return plugin.getLogger();
    }

    @Override
    public @NotNull Path getDataPath() {
        return plugin.getDataFolder().toPath().toAbsolutePath();
    }
}
