package net.code4me.anoduels.bukkit;

import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.bukkit.component.BukkitPlayerComponent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public final class BukkitPlayerFactory implements PlayerComponent.PlayerFactory {
    @NotNull
    private final DuelPlugin plugin;

    public BukkitPlayerFactory(@NotNull DuelPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull PlayerComponent create(@NotNull UUID uniqueId, @NotNull String name) {
        return BukkitPlayerComponent.create(plugin, uniqueId, name);
    }
}
