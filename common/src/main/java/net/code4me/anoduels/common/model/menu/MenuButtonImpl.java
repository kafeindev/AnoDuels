package net.code4me.anoduels.common.model.menu;

import net.code4me.anoduels.api.component.ItemComponent;
import net.code4me.anoduels.api.component.PlayerComponent;
import net.code4me.anoduels.api.model.menu.MenuButton;
import net.code4me.anoduels.api.model.Pair;
import net.code4me.anoduels.common.util.PairFactory;
import ninja.leaping.configurate.ConfigurationNode;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.BiFunction;

public final class MenuButtonImpl implements MenuButton {
    @NotNull
    public static MenuButton create(@NotNull ConfigurationNode node, @NotNull String name, int[] slots,
                                    @NotNull BiFunction<PlayerComponent, Integer, Map<Integer, ItemComponent<?>>> itemCreation) {
        return new MenuButtonImpl(node, name, slots, itemCreation);
    }

    @NotNull
    private final ConfigurationNode node;

    @NotNull
    private final String name;

    private final int[] slots;

    @NotNull
    private final BiFunction<PlayerComponent, Integer, Map<Integer, ItemComponent<?>>> itemCreation;

    private String clickSound;

    private MenuButtonImpl(@NotNull ConfigurationNode node, @NotNull String name, int[] slots,
                           @NotNull BiFunction<PlayerComponent, Integer, Map<Integer, ItemComponent<?>>> itemCreation) {
        this.node = node;
        this.name = name;
        this.slots = slots;
        this.itemCreation = itemCreation;

        this.clickSound = node.getNode("click-sound").getString();
    }

    @Override
    public @NotNull ConfigurationNode getNode() {
        return this.node;
    }

    @Override
    public @NotNull String getName() {
        return this.name;
    }

    @Override
    public int[] getSlots() {
        return this.slots;
    }

    @Override
    public boolean containsSlot(int slot) {
        for (int s : this.slots) {
            if (s == slot) {
                return true;
            }
        }
        return false;
    }

    @Override
    public @Nullable String getClickSound() {
        return this.clickSound;
    }

    @Override
    public @NotNull BiFunction<PlayerComponent, Integer, Map<Integer, ItemComponent<?>>> getItemCreation() {
        return this.itemCreation;
    }

    @Override
    public @NotNull Map<Integer, ItemComponent<?>> createItem(@NotNull PlayerComponent player, int page) {
        return this.itemCreation.apply(player, page);
    }
}
