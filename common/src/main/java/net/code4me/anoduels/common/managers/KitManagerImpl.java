package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.managers.KitManager;
import net.code4me.anoduels.api.model.Kit;
import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.common.config.misc.NodeProcessor;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.KitImpl;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public final class KitManagerImpl extends AbstractManager<String, Kit> implements KitManager {
    @NotNull
    private final DuelPlugin plugin;

    public KitManagerImpl(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        map.putAll(loadAll().join());
    }

    @Override
    public @NotNull Kit create(@NotNull String name) {
        return KitImpl.create(name);
    }

    @Override
    public void delete(@NotNull String name) {
        remove(name);

        Path path = plugin.getDataPath().resolve("kits").resolve(name + ".json");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Kit fromNode(@NotNull ConfigurationNode node) {
        return KitImpl.fromNode(plugin, node);
    }

    @Override
    public @NotNull CompletableFuture<Map<String, Kit>> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            Path parentPath = plugin.getDataPath()
                    .resolve("kits");
            FileProcessor.processDirectory(parentPath);

            Map<String, Kit> kits = new ConcurrentHashMap<>();
            try {
                Files.list(parentPath).forEach(path -> {
                    Kit kit = load(path).join();

                    kits.put(kit.getName(), kit);
                });

                return kits;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Kit> load(@NotNull String name) {
        Path path = plugin.getDataPath()
                .resolve("kits")
                .resolve(name + ".json");
        FileProcessor.processFile(path);

        return load(path);
    }

    @Override
    public @NotNull CompletableFuture<Kit> load(@NotNull Path path) {
        return CompletableFuture.supplyAsync(() -> {
            ConfigurationNode node = NodeProcessor.processNode(path, true);

            return fromNode(node);
        });
    }

    @Override
    public void saveAll(@NotNull Map<String, Kit> kits) {
        CompletableFuture.runAsync(() -> {
            Path parentPath = plugin.getDataPath().resolve("kits");
            FileProcessor.processDirectory(parentPath);

            kits.values().forEach(this::save);
        });
    }

    @Override
    public void save(@NotNull Kit kit) {
        CompletableFuture.runAsync(() -> {
            Path path = plugin.getDataPath()
                    .resolve("kits")
                    .resolve(kit.getName() + ".json");
            FileProcessor.processFile(path);

            ConfigurationLoader<ConfigurationNode> loader = NodeProcessor.createGsonLoader(path.toFile());
            ConfigurationNode node = loader.createEmptyNode();
            try {
                kit.save(plugin, node);

                loader.save(node);
            } catch (ObjectMappingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
