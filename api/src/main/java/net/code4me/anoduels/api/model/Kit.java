package net.code4me.anoduels.api.model;

import net.code4me.anoduels.api.component.ItemComponent;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Kit {
    void initialize(@NotNull ConfigurationNode node) throws ObjectMappingException;

    void save(@NotNull ConfigurationNode node) throws ObjectMappingException;

    @NotNull
    String getName();

    @Nullable
    ItemComponent<?> getIcon();

    void setIcon(@NotNull ItemComponent<?> icon);

    @Nullable
    ItemComponent<?>[] getContents();

    void setContents(@NotNull ItemComponent<?>[] items);
}
