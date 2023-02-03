package net.code4me.anoduels.api.model.menu;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;

public interface Menu {
    void initialize(@NotNull UnaryOperator<String> colorizeFunction);

    @NotNull Map<Integer, ItemComponent<?>> createItems(@NotNull PlayerComponent playerComponent);

    void open(@NotNull PlayerComponent playerComponent);

    void close(@NotNull PlayerComponent playerComponent);

    boolean click(@NotNull PlayerComponent playerComponent, int slot,
                  @NotNull ItemComponent<?> item);

    default boolean click(@NotNull PlayerComponent playerComponent, int slot,
                          @NotNull ItemComponent<?> item, @NotNull ItemComponent<?> cursor) {
        return click(playerComponent, slot, item);
    }

    boolean clickPlayerInventory(@NotNull PlayerComponent playerComponent, int slot,
                                 @NotNull ItemComponent<?> item, @NotNull ItemComponent<?> cursor);

    @NotNull ConfigurationNode getNode();

    @NotNull String getName();

    @NotNull String getTitle();

    int getSize();

    boolean playersCanMoveItems();

    @Nullable ItemComponent<?> getFillerItem();

    @NotNull
    Set<PlayerComponent> getViewers();

    boolean isViewing(@NotNull PlayerComponent playerComponent);

    void setViewersInventoryItem(@NotNull PlayerComponent playerComponent, int slot, @Nullable ItemComponent<?> itemComponent);
}
