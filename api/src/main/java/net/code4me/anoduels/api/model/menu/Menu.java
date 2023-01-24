package net.code4me.anoduels.api.model.menu;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface Menu {
    void initialize();

    void createButtons(@NotNull ConfigurationNode node);

    void open(@NotNull PlayerComponent playerComponent);

    void close(@NotNull PlayerComponent playerComponent);

    void click(@NotNull PlayerComponent playerComponent, @NotNull ItemComponent<?> item, int slot);

    @NotNull
    ConfigurationNode getNode();

    @NotNull
    String getName();

    @NotNull
    String getTitle();

    int getSize();

    @Nullable
    ItemComponent<?> getFillerItem();

    @NotNull
    Set<MenuButton> getButtons();

    void putButton(@NotNull MenuButton button);

    void removeButton(@NotNull MenuButton button);

    @NotNull
    Optional<MenuButton> findButtonByName(@NotNull String name);

    @NotNull
    Optional<MenuButton> findButtonBySlot(int slot);
}
