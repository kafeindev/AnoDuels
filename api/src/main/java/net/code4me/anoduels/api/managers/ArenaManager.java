package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.Arena;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface ArenaManager extends Manager<String, Arena> {
    void initialize();

    void shutdown();

    @NotNull
    Arena create(@NotNull String name);

    void delete(@NotNull String name);

    @NotNull
    Arena fromNode(@NotNull ConfigurationNode node);

    @NotNull
    CompletableFuture<Map<String, Arena>> loadAll();

    @NotNull
    CompletableFuture<Arena> load(@NotNull String name);

    @NotNull
    CompletableFuture<Arena> load(@NotNull Path path);

    void saveAll(@NotNull Map<String, Arena> arenas);

    void save(@NotNull Arena arena);
}
