package net.code4me.anoduels.common.managers;

import com.google.common.reflect.TypeToken;
import net.code4me.anoduels.api.managers.ArenaManager;
import net.code4me.anoduels.api.model.Arena;
import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.common.config.misc.NodeProcessor;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.ArenaImpl;
import net.code4me.anoduels.common.plugin.DuelPlugin;
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

public final class ArenaManagerImpl extends AbstractManager<String, Arena> implements ArenaManager {
    @NotNull
    private final DuelPlugin plugin;

    public ArenaManagerImpl(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void initialize() {
        map.putAll(loadAll().join());
    }

    @Override
    public void shutdown() {
        saveAll(new ConcurrentHashMap<>(map));
    }

    @Override
    public @NotNull Arena create(@NotNull String name) {
        return ArenaImpl.create(name);
    }

    @Override
    public void delete(@NotNull String name) {
        remove(name);

        Path path = plugin.getDataPath().resolve("arenas").resolve(name + ".json");
        try {
            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Arena fromNode(@NotNull ConfigurationNode node) {
        return ArenaImpl.fromNode(node);
    }

    @Override
    public @NotNull CompletableFuture<Map<String, Arena>> loadAll() {
        return CompletableFuture.supplyAsync(() -> {
            Path parentPath = plugin.getDataPath()
                    .resolve("arenas");
            FileProcessor.processDirectory(parentPath);

            Map<String, Arena> arenas = new ConcurrentHashMap<>();
            try {
                Files.list(parentPath).forEach(path -> {
                    Arena arena = load(path).join();

                    arenas.put(arena.getName(), arena);
                });

                return arenas;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Arena> load(@NotNull String name) {
        Path path = plugin.getDataPath()
                .resolve("arenas")
                .resolve(name + ".json");
        FileProcessor.processFile(path);

        return load(path);
    }

    @Override
    public @NotNull CompletableFuture<Arena> load(@NotNull Path path) {
        return CompletableFuture.supplyAsync(() -> {
            ConfigurationNode node = NodeProcessor.processNode(path, true);

            try {
                return node.getValue(TypeToken.of(Arena.class));
            } catch (ObjectMappingException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void saveAll(@NotNull Map<String, Arena> arenas) {
        CompletableFuture.runAsync(() -> {
            Path parentPath = plugin.getDataPath().resolve("arenas");
            FileProcessor.processDirectory(parentPath);

            arenas.values().forEach(this::save);
        });
    }

    @Override
    public void save(@NotNull Arena arena) {
        CompletableFuture.runAsync(() -> {
            Path path = plugin.getDataPath()
                    .resolve("arenas")
                    .resolve(arena.getName() + ".json");
            FileProcessor.processFile(path);

            ConfigurationLoader<ConfigurationNode> loader = NodeProcessor.createGsonLoader(path.toFile());
            ConfigurationNode node = loader.createEmptyNode();
            try {
                node.setValue(TypeToken.of(Arena.class), arena);

                loader.save(node);
            } catch (ObjectMappingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
