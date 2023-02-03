package net.code4me.anoduels.api.model.arena;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;

public interface ArenaCategory {
    void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    @NotNull
    String getName();

    @NotNull
    String getDescription();

    void setDescription(@NotNull String description);

    @NotNull
    ItemComponent<?> getIcon();

    void setIcon(@NotNull ItemComponent<?> icon);
}
