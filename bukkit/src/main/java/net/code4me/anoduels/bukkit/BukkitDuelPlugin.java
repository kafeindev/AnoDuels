package net.code4me.anoduels.bukkit;

import com.github.kafeintr.commands.bukkit.BukkitCommandManager;
import com.github.kafeintr.commands.common.command.CommandManager;
import com.google.common.collect.ImmutableList;
import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.bukkit.commands.DuelCommand;
import net.code4me.anoduels.bukkit.commands.admin.DuelAdminCommand;
import net.code4me.anoduels.bukkit.commands.admin.DuelArenaCommand;
import net.code4me.anoduels.bukkit.commands.admin.DuelKitCommand;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import net.code4me.anoduels.bukkit.component.BukkitPlayerComponent;
import net.code4me.anoduels.bukkit.listener.PlayerListener;
import net.code4me.anoduels.bukkit.listener.registry.ListenerRegistry;
import net.code4me.anoduels.common.config.key.ConfigKeys;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.plugin.AbstractDuelPlugin;
import net.code4me.anoduels.common.plugin.scheduler.concurrent.ConcurrentTaskScheduler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import org.bukkit.Bukkit;
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
            PlayerListener.class
    );

    @NotNull
    private final Plugin plugin;

    public BukkitDuelPlugin(@NotNull Plugin plugin) {
        this.plugin = plugin;

        TypeSerializerCollection.defaults()
                .register(new TypeToken<ItemComponent<ItemStack>>(){}, new BukkitItemComponent.Adapter());
    }

    @Override
    protected void registerCommands() {
        getCommandManager().registerCommand(new DuelCommand(this), new DuelAdminCommand(),
                new DuelArenaCommand(this), new DuelKitCommand(this));
    }

    @Override
    protected void registerListeners() {
        ListenerRegistry.register(this, LISTENERS);
    }

    @Override
    protected @NotNull ConcurrentTaskScheduler createTaskScheduler() {
        ConcurrentTaskScheduler scheduler = new ConcurrentTaskScheduler(getLogger());

        ForkJoinPool forkJoinPool = scheduler.createWorkerPoolBuilder()
                .setDaemon(true)
                .setAsyncMode(true)
                .build();
        scheduler.setWorkerPool(forkJoinPool);

        return scheduler;
    }

    @Override
    protected @NotNull Audience createAudience() {
        return BukkitAudiences.create(plugin).all();
    }

    @Override
    protected @NotNull CommandManager<String> createCommandManager() {
        BukkitCommandManager commandManager = new BukkitCommandManager(plugin);
        commandManager.registerContext(PlayerComponent.class, ((sender, args, arg, parameter) -> {
            if (!sender.isPlayer()) {
                sender.sendMessage(ConfigKeys.Language.NO_PLAYER.getValue());
                return null;
            }

            Player player = Bukkit.getPlayer(sender.getUniqueId());
            return BukkitPlayerComponent.fromPlayer(player);
        }));

        return commandManager;
    }

    @Override
    protected @NotNull ItemComponent.ItemCreator createItemCreator() {
        return new BukkitItemCreator();
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
