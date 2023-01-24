package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.LocationComponent;
import net.code4me.anoduels.api.shape.Cuboid;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public interface Arena {
    void initialize(@NotNull ConfigurationNode node) throws ObjectMappingException;

    void save(@NotNull ConfigurationNode node) throws ObjectMappingException;

    @NotNull
    String getName();

    @Nullable
    Cuboid getBounds();

    void setBounds(@NotNull Cuboid bounds);

    @NotNull
    String getWorld();

    void setWorld(@NotNull String world);

    @Nullable
    LocationComponent getPoint(int index);

    void setPoint(int index, @NotNull LocationComponent spawnPoint);

    @Nullable
    ItemComponent<?> getIcon();

    void setIcon(@NotNull ItemComponent<?> icon);

    @Nullable
    UUID getCurrentMatch();

    void setCurrentMatch(@Nullable UUID currentMatch);

    default boolean isMatchInProgress() {
        return getCurrentMatch() != null;
    }
}
