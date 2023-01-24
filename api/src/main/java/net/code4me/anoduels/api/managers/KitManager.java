package net.code4me.anoduels.api.managers;

import net.code4me.anoduels.api.manager.Manager;
import net.code4me.anoduels.api.model.Kit;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface KitManager extends Manager<String, Kit> {
    void initialize();

    void shutdown();

    @NotNull
    Kit create(@NotNull String name);

    void delete(@NotNull String name);

    @NotNull
    Kit fromNode(@NotNull ConfigurationNode node);

    @NotNull
    CompletableFuture<Map<String, Kit>> loadAll();

    @NotNull
    CompletableFuture<Kit> load(@NotNull String name);

    @NotNull
    CompletableFuture<Kit> load(@NotNull Path path);

    void saveAll(@NotNull Map<String, Kit> kits);

    void save(@NotNull Kit kit);
}
