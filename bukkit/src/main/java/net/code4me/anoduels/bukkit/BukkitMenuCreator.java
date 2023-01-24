package net.code4me.anoduels.bukkit;

import net.code4me.anoduels.api.model.menu.Menu;
import net.code4me.anoduels.common.config.injection.ResourceInjector;
import net.code4me.anoduels.common.config.misc.NodeProcessor;
import net.code4me.anoduels.common.managers.MenuManagerImpl;
import net.code4me.anoduels.common.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public final class BukkitMenuCreator extends MenuManagerImpl.MenuCreator {
    private final ResourceInjector resourceInjector = new ResourceInjector();

    @NotNull
    private final Path parentDirectory;

    public BukkitMenuCreator(@NotNull DuelPlugin plugin) {
        super(plugin);
        this.parentDirectory = plugin.getDataPath().resolve("menus");
    }

    @Override
    public @NotNull Menu create(@NotNull MenuManagerImpl.MenuType type) {
        return CompletableFuture.supplyAsync(() -> {
            String fileName = type.getName() + "-menu.yml";
            Path path = parentDirectory.resolve(fileName);
            if (!Files.exists(path)) {
                resourceInjector.inject(path, type.getClazz(), "menus/" + fileName);
            }

            ConfigurationNode node = NodeProcessor.processNode(path).getNode("menu");
            try {
                Menu menu = type.getClazz()
                        .getConstructor(DuelPlugin.class, ConfigurationNode.class)
                        .newInstance(plugin, node);
                menu.initialize();

                return menu;
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException("Failed to load menu: " + type.name(), e);
            }
        }).join();
    }
}
