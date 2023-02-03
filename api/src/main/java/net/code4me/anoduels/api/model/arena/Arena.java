package net.code4me.anoduels.api.model.arena;

import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.model.plugin.DuelPlugin;
import net.code4me.anoduels.api.shape.Cuboid;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Arena {
    void initialize(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    void save(@NotNull DuelPlugin plugin, @NotNull ConfigurationNode node) throws ObjectMappingException;

    @NotNull String getName();

    @NotNull String getCategory();

    @Nullable Cuboid getBounds();

    void setBounds(@NotNull Cuboid bounds);

    @Nullable LocationComponent getPoint(int index);

    void setPoint(int index, @NotNull LocationComponent spawnPoint);

    @Nullable UUID getCurrentMatchId();

    void setCurrentMatchId(@Nullable UUID currentMatch);

    default boolean isMatchInProgress() {
        return getCurrentMatchId() != null;
    }
}
