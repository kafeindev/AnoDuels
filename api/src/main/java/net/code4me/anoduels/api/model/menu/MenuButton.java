package net.code4me.anoduels.api.model.menu;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;

public interface MenuButton {
    @NotNull
    ConfigurationNode getNode();

    @NotNull
    String getName();

    int[] getSlots();

    boolean containsSlot(int slot);

    @Nullable
    String getClickSound();

    @NotNull
    BiFunction<PlayerComponent, Integer, Map<Integer, ItemComponent<?>>> getItemCreation();

    @NotNull
    Map<Integer, ItemComponent<?>> createItem(@NotNull PlayerComponent player, int page);
}
