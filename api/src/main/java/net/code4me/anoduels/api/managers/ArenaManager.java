package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.arena.Arena;
import net.code4me.anoduels.api.model.arena.ArenaCategory;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface ArenaManager extends Manager<String, Arena> {
    void initialize();

    @NotNull Arena createArena(@NotNull String name, @NotNull String category);

    @NotNull Arena createArenaFromNode(@NotNull ConfigurationNode node);

    @NotNull ArenaCategory createCategory(@NotNull String name, @NotNull String description,
                                          @NotNull ItemComponent<?> icon);

    @NotNull ArenaCategory createCategoryFromNode(@NotNull ConfigurationNode node);

    @NotNull
    Set<Arena> findArenasByCategory(@NotNull String category);

    @NotNull
    Optional<Arena> findUnusedArenaByCategory(@NotNull String category);

    @NotNull
    Map<String, ArenaCategory> getCategories();

    @NotNull
    Optional<ArenaCategory> findCategory(@NotNull String name);

    @NotNull CompletableFuture<Map<String, Arena>> loadAllArenas();

    @NotNull CompletableFuture<Map<String, ArenaCategory>> loadAllCategories();

    @NotNull CompletableFuture<Arena> loadArena(@NotNull String name);

    @NotNull CompletableFuture<Arena> loadArena(@NotNull Path path);

    @NotNull CompletableFuture<ArenaCategory> loadCategory(@NotNull String name);

    @NotNull CompletableFuture<ArenaCategory> loadCategory(@NotNull Path path);

    void saveAllArenas(@NotNull Map<String, Arena> arenas);

    void saveAllCategories(@NotNull Map<String, ArenaCategory> categories);

    void saveArena(@NotNull Arena arena);

    void saveCategory(@NotNull ArenaCategory category);

    void deleteArena(@NotNull String name);

    void deleteCategory(@NotNull String name);

    void putCategory(@NotNull ArenaCategory category);

    void removeCategory(@NotNull String name);
}
