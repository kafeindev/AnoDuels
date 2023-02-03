package net.code4me.anoduels.common.managers;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.managers.ArenaManager;
import net.code4me.anoduels.api.model.arena.Arena;
import net.code4me.anoduels.api.model.arena.ArenaCategory;
import net.code4me.anoduels.common.config.misc.FileProcessor;
import net.code4me.anoduels.common.config.misc.NodeProcessor;
import net.code4me.anoduels.common.manager.AbstractManager;
import net.code4me.anoduels.common.model.arena.ArenaCategoryImpl;
import net.code4me.anoduels.common.model.arena.ArenaImpl;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class ArenaManagerImpl extends AbstractManager<String, Arena> implements ArenaManager {
    private final Map<String, ArenaCategory> categories = new ConcurrentHashMap<>();

    @NotNull
    private final DuelPlugin plugin;

    @NotNull
    private final Path arenaPath;

    public ArenaManagerImpl(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
        this.arenaPath = plugin.getDataPath().resolve("arena");

        FileProcessor.processDirectory(this.arenaPath);
        FileProcessor.processDirectory(this.arenaPath.resolve("arenas"));
        FileProcessor.processDirectory(this.arenaPath.resolve("categories"));
    }

    @Override
    public void initialize() {
        this.map.putAll(loadAllArenas().join());
        this.categories.putAll(loadAllCategories().join());
    }

    @Override
    public @NotNull Arena createArena(@NotNull String name, @NotNull String category) {
        return ArenaImpl.create(name, category);
    }

    @Override
    public @NotNull Arena createArenaFromNode(@NotNull ConfigurationNode node) {
        return ArenaImpl.fromNode(this.plugin, node);
    }

    @Override
    public @NotNull ArenaCategory createCategory(@NotNull String name, @NotNull String description,
                                                 @NotNull ItemComponent<?> icon) {
        return ArenaCategoryImpl.create(name, description, icon);
    }

    @Override
    public @NotNull ArenaCategory createCategoryFromNode(@NotNull ConfigurationNode node) {
        return ArenaCategoryImpl.fromNode(this.plugin, node);
    }

    @Override
    public @NotNull Set<Arena> findArenasByCategory(@NotNull String category) {
        return this.map.values().stream()
                .filter(arena -> arena.getCategory().equals(category))
                .collect(Collectors.toSet());
    }

    @Override
    public @NotNull Optional<Arena> findUnusedArenaByCategory(@NotNull String category) {
        return this.map.values().stream()
                .filter(arena -> arena.getCategory().equals(category))
                .filter(arena -> !arena.isMatchInProgress())
                .findFirst();
    }

    @Override
    public @NotNull Map<String, ArenaCategory> getCategories() {
        return this.categories;
    }

    @Override
    public @NotNull Optional<ArenaCategory> findCategory(@NotNull String name) {
        return Optional.ofNullable(this.categories.get(name));
    }

    @Override
    public @NotNull CompletableFuture<Map<String, Arena>> loadAllArenas() {
        return CompletableFuture.supplyAsync(() -> {
            Path parentPath = this.arenaPath.resolve("arenas");
            FileProcessor.processDirectory(parentPath);

            Map<String, Arena> arenas = new ConcurrentHashMap<>();
            try {
                Files.list(parentPath).forEach(path -> {
                    Arena arena = loadArena(path).join();

                    arenas.put(arena.getName(), arena);
                });

                return arenas;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Map<String, ArenaCategory>> loadAllCategories() {
        return CompletableFuture.supplyAsync(() -> {
            Path parentPath = this.arenaPath.resolve("categories");
            FileProcessor.processDirectory(parentPath);

            Map<String, ArenaCategory> categories = new ConcurrentHashMap<>();
            try {
                Files.list(parentPath).forEach(path -> {
                    ArenaCategory category = loadCategory(path).join();

                    categories.put(category.getName(), category);
                });

                return categories;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public @NotNull CompletableFuture<Arena> loadArena(@NotNull String name) {
        Path path = this.arenaPath.resolve("arenas")
                .resolve(name + ".json");
        FileProcessor.processFile(path);

        return loadArena(path);
    }

    @Override
    public @NotNull CompletableFuture<Arena> loadArena(@NotNull Path path) {
        return CompletableFuture.supplyAsync(() -> {
            ConfigurationNode node = NodeProcessor.processNode(path, true);

            return ArenaImpl.fromNode(this.plugin, node);
        });
    }

    @Override
    public @NotNull CompletableFuture<ArenaCategory> loadCategory(@NotNull String name) {
        Path path = this.arenaPath
                .resolve("categories")
                .resolve(name + ".json");
        FileProcessor.processFile(path);

        return loadCategory(path);
    }

    @Override
    public @NotNull CompletableFuture<ArenaCategory> loadCategory(@NotNull Path path) {
        return CompletableFuture.supplyAsync(() -> {
            ConfigurationNode node = NodeProcessor.processNode(path, true);

            return ArenaCategoryImpl.fromNode(this.plugin, node);
        });
    }

    @Override
    public void saveAllArenas(@NotNull Map<String, Arena> arenas) {
        CompletableFuture.runAsync(() -> {
            Path parentPath = this.arenaPath
                    .resolve("arenas");
            FileProcessor.processDirectory(parentPath);

            arenas.values().forEach(this::saveArena);
        });
    }

    @Override
    public void saveAllCategories(@NotNull Map<String, ArenaCategory> categories) {
        CompletableFuture.runAsync(() -> {
            Path parentPath = this.arenaPath
                    .resolve("categories");
            FileProcessor.processDirectory(parentPath);

            categories.values().forEach(this::saveCategory);
        });
    }

    @Override
    public void saveArena(@NotNull Arena arena) {
        CompletableFuture.runAsync(() -> {
            Path path = this.arenaPath
                    .resolve("arenas")
                    .resolve(arena.getName() + ".json");
            FileProcessor.processFile(path);

            ConfigurationLoader<ConfigurationNode> loader = NodeProcessor.createGsonLoader(path.toFile());
            ConfigurationNode node = loader.createEmptyNode();
            try {
                arena.save(this.plugin, node);

                loader.save(node);
            } catch (ObjectMappingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void saveCategory(@NotNull ArenaCategory category) {
        CompletableFuture.runAsync(() -> {
            Path path = this.arenaPath
                    .resolve("categories")
                    .resolve(category.getName() + ".json");
            FileProcessor.processFile(path);

            ConfigurationLoader<ConfigurationNode> loader = NodeProcessor.createGsonLoader(path.toFile());
            ConfigurationNode node = loader.createEmptyNode();
            try {
                category.save(this.plugin, node);

                loader.save(node);
            } catch (ObjectMappingException | IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void deleteArena(@NotNull String name) {
        remove(name);

        try {
            Path path = this.arenaPath
                    .resolve("arenas")
                    .resolve(name + ".json");

            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteCategory(@NotNull String name) {
        this.categories.remove(name);

        try {
            Path path = this.arenaPath
                    .resolve("categories")
                    .resolve(name + ".json");

            Files.delete(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void putCategory(@NotNull ArenaCategory category) {
        this.categories.put(category.getName(), category);
    }

    @Override
    public void removeCategory(@NotNull String name) {
        this.categories.remove(name);
    }
}
