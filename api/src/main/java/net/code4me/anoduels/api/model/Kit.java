package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Kit {
    void initialize(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    @NotNull String getName();

    @Nullable ItemComponent<?> getIcon();

    void setIcon(@NotNull ItemComponent<?> icon);

    @Nullable ItemComponent<?>[] getItems();

    @Nullable ItemComponent<?>[] getArmors();

    void setContents(@NotNull ItemComponent<?>[] items, @NotNull ItemComponent<?>[] armors);
}
