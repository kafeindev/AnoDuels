package net.code4me.anoduels.bukkit;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.bukkit.component.BukkitItemComponent;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class BukkitItemCreator implements ItemComponent.ItemCreator {
    @Override
    public @NotNull ItemComponent<?> fromBase64(@NotNull String base64) {
        return BukkitItemComponent.fromBase64(base64);
    }

    @Override
    public @NotNull ItemComponent<?> fromNode(@NotNull ConfigurationNode node, @NotNull Map<String, String> placeholders) {
        return BukkitItemComponent.fromNode(node, placeholders);
    }

    @Override
    public @NotNull ItemComponent<?> create(@NotNull String materialName, int amount) {
        return BukkitItemComponent.create(materialName, amount);
    }
}
